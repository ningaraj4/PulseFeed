package main

import (
	"log"
	"os"

	"pulsefeed-backend/internal/config"
	"pulsefeed-backend/internal/database"
	"pulsefeed-backend/internal/handlers"
	"pulsefeed-backend/internal/middleware"
	"pulsefeed-backend/internal/redis"
	"pulsefeed-backend/internal/websocket"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
)

func main() {
	// Load environment variables
	if err := godotenv.Load(); err != nil {
		log.Println("No .env file found")
	}

	// Initialize configuration
	cfg := config.Load()

	// Initialize database
	db, err := database.Connect(cfg.DatabaseURL)
	if err != nil {
		log.Fatal("Failed to connect to database:", err)
	}
	defer db.Close()

	// Run migrations
	if err := database.Migrate(db); err != nil {
		log.Fatal("Failed to run migrations:", err)
	}

	// Initialize Redis
	redisClient := redis.Connect(cfg.RedisURL)
	defer redisClient.Close()

	// Initialize WebSocket hub
	hub := websocket.NewHub()
	go hub.Run()

	// Initialize handlers
	h := handlers.New(db, redisClient, hub)

	// Setup Gin router
	r := gin.Default()

	// CORS middleware
	r.Use(middleware.CORS())

	// API routes
	api := r.Group("/api/v1")
	{
		// Auth routes
		auth := api.Group("/auth")
		{
			auth.POST("/register", h.Register)
			auth.POST("/login", h.Login)
			auth.POST("/refresh", h.RefreshToken)
			auth.POST("/google", h.GoogleAuth)
		}

		// Protected routes
		protected := api.Group("/")
		protected.Use(middleware.AuthRequired())
		{
			// User routes
			users := protected.Group("/users")
			{
				users.GET("/me", h.GetProfile)
				users.PUT("/me", h.UpdateProfile)
				users.GET("/:id", h.GetUserProfile)
				users.POST("/:id/follow", h.FollowUser)
				users.DELETE("/:id/follow", h.UnfollowUser)
				users.GET("/:id/followers", h.GetFollowers)
				users.GET("/:id/following", h.GetFollowing)
				users.GET("/search", h.SearchUsers)
			}

			// Post routes
			posts := protected.Group("/posts")
			{
				posts.POST("/", h.CreatePost)
				posts.GET("/feed", h.GetFeed)
				posts.GET("/:id", h.GetPost)
				posts.POST("/:id/like", h.LikePost)
				posts.DELETE("/:id/like", h.UnlikePost)
				posts.GET("/:id/comments", h.GetComments)
				posts.POST("/:id/comments", h.CreateComment)
				posts.GET("/user/:id", h.GetUserPosts)
			}

			// Notification routes
			notifications := protected.Group("/notifications")
			{
				notifications.GET("/", h.GetNotifications)
				notifications.PUT("/:id/read", h.MarkNotificationRead)
			}

			// Upload routes
			uploads := protected.Group("/uploads")
			{
				uploads.POST("/media", h.UploadMedia)
			}
		}
	}

	// WebSocket endpoint
	r.GET("/ws", h.HandleWebSocket)

	// Start server
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	log.Printf("Server starting on port %s", port)
	if err := r.Run(":" + port); err != nil {
		log.Fatal("Failed to start server:", err)
	}
}
