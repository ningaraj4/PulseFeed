package handlers

import (
	"database/sql"
	"net/http"
	"strconv"
	"time"

	"pulsefeed-backend/internal/models"
	"pulsefeed-backend/internal/redis"

	"github.com/gin-gonic/gin"
)

func (h *Handler) CreatePost(c *gin.Context) {
	userID := c.GetInt("user_id")
	
	var req models.CreatePostRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	var post models.Post
	err := h.db.QueryRow(`
		INSERT INTO posts (user_id, content, media_urls, media_type) 
		VALUES ($1, $2, $3, $4) 
		RETURNING id, user_id, content, media_urls, media_type, likes_count, comments_count, created_at, updated_at`,
		userID, req.Content, models.MediaURLs(req.MediaURLs), req.MediaType,
	).Scan(&post.ID, &post.UserID, &post.Content, &post.MediaURLs, &post.MediaType,
		&post.LikesCount, &post.CommentsCount, &post.CreatedAt, &post.UpdatedAt)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create post"})
		return
	}

	// Get user info for the post
	user, err := h.getUserWithCounts(userID, userID)
	if err == nil {
		post.User = user
	}

	// Clear feed cache for followers
	h.clearFollowersFeedCache(userID)

	// Broadcast new post via WebSocket
	h.hub.Broadcast(map[string]interface{}{
		"type": "new_post",
		"data": post,
	})

	c.JSON(http.StatusCreated, post)
}

func (h *Handler) GetFeed(c *gin.Context) {
	userID := c.GetInt("user_id")
	
	limit := 20
	if l := c.Query("limit"); l != "" {
		if parsed, err := strconv.Atoi(l); err == nil && parsed > 0 && parsed <= 50 {
			limit = parsed
		}
	}

	offset := 0
	if o := c.Query("offset"); o != "" {
		if parsed, err := strconv.Atoi(o); err == nil && parsed >= 0 {
			offset = parsed
		}
	}

	// Try to get from cache first
	cacheKey := redis.FeedCacheKey(userID)
	var cachedPosts []*models.Post
	if offset == 0 && h.redis.Get(cacheKey, &cachedPosts) == nil && len(cachedPosts) > 0 {
		end := limit
		if end > len(cachedPosts) {
			end = len(cachedPosts)
		}
		
		c.JSON(http.StatusOK, models.FeedResponse{
			Posts:   cachedPosts[:end],
			HasMore: len(cachedPosts) > limit,
		})
		return
	}

	// Get posts from database
	rows, err := h.db.Query(`
		SELECT p.id, p.user_id, p.content, p.media_urls, p.media_type, 
		       p.likes_count, p.comments_count, p.created_at, p.updated_at,
		       u.id, u.username, u.email, u.full_name, u.bio, u.avatar, u.is_verified,
		       u.created_at, u.updated_at,
		       EXISTS(SELECT 1 FROM likes WHERE post_id = p.id AND user_id = $1) as is_liked
		FROM posts p
		JOIN users u ON p.user_id = u.id
		WHERE p.user_id = $1 OR p.user_id IN (
			SELECT following_id FROM follows WHERE follower_id = $1
		)
		ORDER BY p.created_at DESC
		LIMIT $2 OFFSET $3`,
		userID, limit, offset)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get feed"})
		return
	}
	defer rows.Close()

	var posts []*models.Post
	for rows.Next() {
		var post models.Post
		var user models.User
		
		err := rows.Scan(
			&post.ID, &post.UserID, &post.Content, &post.MediaURLs, &post.MediaType,
			&post.LikesCount, &post.CommentsCount, &post.CreatedAt, &post.UpdatedAt,
			&user.ID, &user.Username, &user.Email, &user.FullName, &user.Bio,
			&user.Avatar, &user.IsVerified, &user.CreatedAt, &user.UpdatedAt,
			&post.IsLiked)

		if err != nil {
			continue
		}

		post.User = &user
		posts = append(posts, &post)
	}

	// Cache the feed if it's the first page
	if offset == 0 && len(posts) > 0 {
		h.redis.Set(cacheKey, posts, time.Minute*5)
	}

	c.JSON(http.StatusOK, models.FeedResponse{
		Posts:   posts,
		HasMore: len(posts) == limit,
	})
}

func (h *Handler) GetPost(c *gin.Context) {
	userID := c.GetInt("user_id")
	postID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid post ID"})
		return
	}

	var post models.Post
	var user models.User
	
	err = h.db.QueryRow(`
		SELECT p.id, p.user_id, p.content, p.media_urls, p.media_type, 
		       p.likes_count, p.comments_count, p.created_at, p.updated_at,
		       u.id, u.username, u.email, u.full_name, u.bio, u.avatar, u.is_verified,
		       u.created_at, u.updated_at,
		       EXISTS(SELECT 1 FROM likes WHERE post_id = p.id AND user_id = $2) as is_liked
		FROM posts p
		JOIN users u ON p.user_id = u.id
		WHERE p.id = $1`,
		postID, userID).Scan(
		&post.ID, &post.UserID, &post.Content, &post.MediaURLs, &post.MediaType,
		&post.LikesCount, &post.CommentsCount, &post.CreatedAt, &post.UpdatedAt,
		&user.ID, &user.Username, &user.Email, &user.FullName, &user.Bio,
		&user.Avatar, &user.IsVerified, &user.CreatedAt, &user.UpdatedAt,
		&post.IsLiked)

	if err != nil {
		if err == sql.ErrNoRows {
			c.JSON(http.StatusNotFound, gin.H{"error": "Post not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get post"})
		return
	}

	post.User = &user
	c.JSON(http.StatusOK, post)
}

func (h *Handler) LikePost(c *gin.Context) {
	userID := c.GetInt("user_id")
	postID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid post ID"})
		return
	}

	// Check if post exists and get owner
	var postOwnerID int
	err = h.db.QueryRow("SELECT user_id FROM posts WHERE id = $1", postID).Scan(&postOwnerID)
	if err != nil {
		if err == sql.ErrNoRows {
			c.JSON(http.StatusNotFound, gin.H{"error": "Post not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Database error"})
		return
	}

	// Insert like (will be ignored if already exists due to unique constraint)
	_, err = h.db.Exec("INSERT INTO likes (post_id, user_id) VALUES ($1, $2) ON CONFLICT DO NOTHING", 
		postID, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to like post"})
		return
	}

	// Create notification if not self-like
	if postOwnerID != userID {
		h.createNotification(postOwnerID, models.NotificationLike, userID, &postID)
	}

	// Clear cache
	h.redis.Delete(redis.PostCacheKey(postID))
	h.clearFollowersFeedCache(userID)

	c.JSON(http.StatusOK, gin.H{"message": "Post liked"})
}

func (h *Handler) UnlikePost(c *gin.Context) {
	userID := c.GetInt("user_id")
	postID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid post ID"})
		return
	}

	_, err = h.db.Exec("DELETE FROM likes WHERE post_id = $1 AND user_id = $2", postID, userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to unlike post"})
		return
	}

	// Clear cache
	h.redis.Delete(redis.PostCacheKey(postID))
	h.clearFollowersFeedCache(userID)

	c.JSON(http.StatusOK, gin.H{"message": "Post unliked"})
}

func (h *Handler) GetComments(c *gin.Context) {
	postID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid post ID"})
		return
	}

	rows, err := h.db.Query(`
		SELECT c.id, c.post_id, c.user_id, c.content, c.created_at, c.updated_at,
		       u.id, u.username, u.email, u.full_name, u.bio, u.avatar, u.is_verified,
		       u.created_at, u.updated_at
		FROM comments c
		JOIN users u ON c.user_id = u.id
		WHERE c.post_id = $1
		ORDER BY c.created_at ASC`,
		postID)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get comments"})
		return
	}
	defer rows.Close()

	var comments []*models.Comment
	for rows.Next() {
		var comment models.Comment
		var user models.User
		
		err := rows.Scan(
			&comment.ID, &comment.PostID, &comment.UserID, &comment.Content,
			&comment.CreatedAt, &comment.UpdatedAt,
			&user.ID, &user.Username, &user.Email, &user.FullName, &user.Bio,
			&user.Avatar, &user.IsVerified, &user.CreatedAt, &user.UpdatedAt)

		if err != nil {
			continue
		}

		comment.User = &user
		comments = append(comments, &comment)
	}

	c.JSON(http.StatusOK, comments)
}

func (h *Handler) CreateComment(c *gin.Context) {
	userID := c.GetInt("user_id")
	postID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid post ID"})
		return
	}

	var req models.CreateCommentRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Check if post exists and get owner
	var postOwnerID int
	err = h.db.QueryRow("SELECT user_id FROM posts WHERE id = $1", postID).Scan(&postOwnerID)
	if err != nil {
		if err == sql.ErrNoRows {
			c.JSON(http.StatusNotFound, gin.H{"error": "Post not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Database error"})
		return
	}

	var comment models.Comment
	err = h.db.QueryRow(`
		INSERT INTO comments (post_id, user_id, content) 
		VALUES ($1, $2, $3) 
		RETURNING id, post_id, user_id, content, created_at, updated_at`,
		postID, userID, req.Content,
	).Scan(&comment.ID, &comment.PostID, &comment.UserID, &comment.Content,
		&comment.CreatedAt, &comment.UpdatedAt)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create comment"})
		return
	}

	// Get user info for the comment
	user, err := h.getUserWithCounts(userID, userID)
	if err == nil {
		comment.User = user
	}

	// Create notification if not self-comment
	if postOwnerID != userID {
		h.createNotification(postOwnerID, models.NotificationComment, userID, &postID)
	}

	// Clear cache
	h.redis.Delete(redis.PostCacheKey(postID))

	c.JSON(http.StatusCreated, comment)
}

func (h *Handler) GetUserPosts(c *gin.Context) {
	currentUserID := c.GetInt("user_id")
	userID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid user ID"})
		return
	}

	limit := 20
	if l := c.Query("limit"); l != "" {
		if parsed, err := strconv.Atoi(l); err == nil && parsed > 0 && parsed <= 50 {
			limit = parsed
		}
	}

	offset := 0
	if o := c.Query("offset"); o != "" {
		if parsed, err := strconv.Atoi(o); err == nil && parsed >= 0 {
			offset = parsed
		}
	}

	rows, err := h.db.Query(`
		SELECT p.id, p.user_id, p.content, p.media_urls, p.media_type, 
		       p.likes_count, p.comments_count, p.created_at, p.updated_at,
		       u.id, u.username, u.email, u.full_name, u.bio, u.avatar, u.is_verified,
		       u.created_at, u.updated_at,
		       EXISTS(SELECT 1 FROM likes WHERE post_id = p.id AND user_id = $2) as is_liked
		FROM posts p
		JOIN users u ON p.user_id = u.id
		WHERE p.user_id = $1
		ORDER BY p.created_at DESC
		LIMIT $3 OFFSET $4`,
		userID, currentUserID, limit, offset)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get user posts"})
		return
	}
	defer rows.Close()

	var posts []*models.Post
	for rows.Next() {
		var post models.Post
		var user models.User
		
		err := rows.Scan(
			&post.ID, &post.UserID, &post.Content, &post.MediaURLs, &post.MediaType,
			&post.LikesCount, &post.CommentsCount, &post.CreatedAt, &post.UpdatedAt,
			&user.ID, &user.Username, &user.Email, &user.FullName, &user.Bio,
			&user.Avatar, &user.IsVerified, &user.CreatedAt, &user.UpdatedAt,
			&post.IsLiked)

		if err != nil {
			continue
		}

		post.User = &user
		posts = append(posts, &post)
	}

	c.JSON(http.StatusOK, models.FeedResponse{
		Posts:   posts,
		HasMore: len(posts) == limit,
	})
}

// Helper functions
func (h *Handler) clearFollowersFeedCache(userID int) {
	// Get followers and clear their feed cache
	rows, err := h.db.Query("SELECT follower_id FROM follows WHERE following_id = $1", userID)
	if err != nil {
		return
	}
	defer rows.Close()

	for rows.Next() {
		var followerID int
		if rows.Scan(&followerID) == nil {
			h.redis.Delete(redis.FeedCacheKey(followerID))
		}
	}
	
	// Also clear own feed cache
	h.redis.Delete(redis.FeedCacheKey(userID))
}

func (h *Handler) createNotification(userID int, notifType models.NotificationType, actorID int, postID *int) {
	_, err := h.db.Exec(`
		INSERT INTO notifications (user_id, type, actor_id, post_id) 
		VALUES ($1, $2, $3, $4)`,
		userID, notifType, actorID, postID)
	
	if err == nil {
		// Send real-time notification
		h.hub.BroadcastToUser(userID, map[string]interface{}{
			"type": "notification",
			"data": map[string]interface{}{
				"type":     notifType,
				"actor_id": actorID,
				"post_id":  postID,
			},
		})
	}
}
