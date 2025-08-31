package handlers

import (
	"database/sql"
	"net/http"
	"strconv"
	"time"

	"pulsefeed-backend/internal/models"
	"pulsefeed-backend/internal/redis"
	"pulsefeed-backend/internal/websocket"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
	gorillaws "github.com/gorilla/websocket"
	"golang.org/x/crypto/bcrypt"
)

type Handler struct {
	db    *sql.DB
	redis *redis.Client
	hub   *websocket.Hub
}

func New(db *sql.DB, redisClient *redis.Client, hub *websocket.Hub) *Handler {
	return &Handler{
		db:    db,
		redis: redisClient,
		hub:   hub,
	}
}

// Auth handlers
func (h *Handler) Register(c *gin.Context) {
	var req models.RegisterRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Check if username or email already exists
	var exists bool
	err := h.db.QueryRow("SELECT EXISTS(SELECT 1 FROM users WHERE username = $1 OR email = $2)", 
		req.Username, req.Email).Scan(&exists)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Database error"})
		return
	}
	if exists {
		c.JSON(http.StatusConflict, gin.H{"error": "Username or email already exists"})
		return
	}

	// Hash password
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to hash password"})
		return
	}

	// Create user
	var user models.User
	err = h.db.QueryRow(`
		INSERT INTO users (username, email, password_hash, full_name) 
		VALUES ($1, $2, $3, $4) 
		RETURNING id, username, email, full_name, bio, avatar, is_verified, created_at, updated_at`,
		req.Username, req.Email, string(hashedPassword), req.FullName,
	).Scan(&user.ID, &user.Username, &user.Email, &user.FullName, &user.Bio, 
		&user.Avatar, &user.IsVerified, &user.CreatedAt, &user.UpdatedAt)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create user"})
		return
	}

	// Generate tokens
	accessToken, refreshToken, err := h.generateTokens(user.ID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to generate tokens"})
		return
	}

	c.JSON(http.StatusCreated, models.AuthResponse{
		User:         &user,
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	})
}

func (h *Handler) Login(c *gin.Context) {
	var req models.LoginRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	var user models.User
	var hashedPassword string
	err := h.db.QueryRow(`
		SELECT id, username, email, password_hash, full_name, bio, avatar, is_verified, created_at, updated_at
		FROM users WHERE username = $1 OR email = $1`,
		req.Username,
	).Scan(&user.ID, &user.Username, &user.Email, &hashedPassword, &user.FullName,
		&user.Bio, &user.Avatar, &user.IsVerified, &user.CreatedAt, &user.UpdatedAt)

	if err != nil {
		if err == sql.ErrNoRows {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid credentials"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Database error"})
		return
	}

	// Check password
	if err := bcrypt.CompareHashAndPassword([]byte(hashedPassword), []byte(req.Password)); err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid credentials"})
		return
	}

	// Generate tokens
	accessToken, refreshToken, err := h.generateTokens(user.ID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to generate tokens"})
		return
	}

	c.JSON(http.StatusOK, models.AuthResponse{
		User:         &user,
		AccessToken:  accessToken,
		RefreshToken: refreshToken,
	})
}

func (h *Handler) RefreshToken(c *gin.Context) {
	// Implementation for refresh token
	c.JSON(http.StatusNotImplemented, gin.H{"error": "Not implemented yet"})
}

func (h *Handler) GoogleAuth(c *gin.Context) {
	// Implementation for Google OAuth
	c.JSON(http.StatusNotImplemented, gin.H{"error": "Not implemented yet"})
}

// User handlers
func (h *Handler) GetProfile(c *gin.Context) {
	userID := c.GetInt("user_id")
	
	user, err := h.getUserWithCounts(userID, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get profile"})
		return
	}

	c.JSON(http.StatusOK, user)
}

func (h *Handler) UpdateProfile(c *gin.Context) {
	userID := c.GetInt("user_id")
	
	var req models.UpdateProfileRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Update user profile
	_, err := h.db.Exec(`
		UPDATE users SET full_name = COALESCE(NULLIF($1, ''), full_name), 
		bio = COALESCE(NULLIF($2, ''), bio), 
		avatar = COALESCE(NULLIF($3, ''), avatar),
		updated_at = CURRENT_TIMESTAMP
		WHERE id = $4`,
		req.FullName, req.Bio, req.Avatar, userID)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update profile"})
		return
	}

	// Get updated user
	user, err := h.getUserWithCounts(userID, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get updated profile"})
		return
	}

	c.JSON(http.StatusOK, user)
}

func (h *Handler) GetUserProfile(c *gin.Context) {
	currentUserID := c.GetInt("user_id")
	userID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid user ID"})
		return
	}

	user, err := h.getUserWithCounts(userID, currentUserID)
	if err != nil {
		if err == sql.ErrNoRows {
			c.JSON(http.StatusNotFound, gin.H{"error": "User not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get user profile"})
		return
	}

	c.JSON(http.StatusOK, user)
}

// Helper functions
func (h *Handler) generateTokens(userID int) (string, string, error) {
	// Access token (15 minutes)
	accessClaims := jwt.MapClaims{
		"user_id": userID,
		"exp":     time.Now().Add(time.Minute * 15).Unix(),
		"iat":     time.Now().Unix(),
	}
	accessToken := jwt.NewWithClaims(jwt.SigningMethodHS256, accessClaims)
	accessTokenString, err := accessToken.SignedString([]byte("your-super-secret-jwt-key"))
	if err != nil {
		return "", "", err
	}

	// Refresh token (7 days)
	refreshClaims := jwt.MapClaims{
		"user_id": userID,
		"exp":     time.Now().Add(time.Hour * 24 * 7).Unix(),
		"iat":     time.Now().Unix(),
	}
	refreshToken := jwt.NewWithClaims(jwt.SigningMethodHS256, refreshClaims)
	refreshTokenString, err := refreshToken.SignedString([]byte("your-super-secret-jwt-key"))
	if err != nil {
		return "", "", err
	}

	return accessTokenString, refreshTokenString, nil
}

func (h *Handler) getUserWithCounts(userID, currentUserID int) (*models.User, error) {
	var user models.User
	
	// Get user with counts and follow status
	err := h.db.QueryRow(`
		SELECT u.id, u.username, u.email, u.full_name, u.bio, u.avatar, u.is_verified, 
		       u.created_at, u.updated_at,
		       (SELECT COUNT(*) FROM follows WHERE following_id = u.id) as followers_count,
		       (SELECT COUNT(*) FROM follows WHERE follower_id = u.id) as following_count,
		       (SELECT COUNT(*) FROM posts WHERE user_id = u.id) as posts_count,
		       CASE WHEN $2 != u.id THEN 
		           EXISTS(SELECT 1 FROM follows WHERE follower_id = $2 AND following_id = u.id)
		       ELSE false END as is_following
		FROM users u WHERE u.id = $1`,
		userID, currentUserID,
	).Scan(&user.ID, &user.Username, &user.Email, &user.FullName, &user.Bio,
		&user.Avatar, &user.IsVerified, &user.CreatedAt, &user.UpdatedAt,
		&user.FollowersCount, &user.FollowingCount, &user.PostsCount, &user.IsFollowing)

	return &user, err
}

// WebSocket handler
func (h *Handler) HandleWebSocket(c *gin.Context) {
	// Get user ID from token
	token := c.Query("token")
	if token == "" {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Token required"})
		return
	}

	parsedToken, err := jwt.Parse(token, func(token *jwt.Token) (interface{}, error) {
		return []byte("your-super-secret-jwt-key"), nil
	})

	if err != nil || !parsedToken.Valid {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid token"})
		return
	}

	claims, ok := parsedToken.Claims.(jwt.MapClaims)
	if !ok {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid token claims"})
		return
	}

	userID := int(claims["user_id"].(float64))

	// Upgrade connection
	upgrader := gorillaws.Upgrader{
		CheckOrigin: func(r *http.Request) bool {
			return true
		},
	}

	conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		return
	}

	h.hub.HandleWebSocket(conn, userID)
}
