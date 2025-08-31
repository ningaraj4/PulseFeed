package handlers

import (
	"net/http"
	"strconv"
	"strings"

	"pulsefeed-backend/internal/models"
	"pulsefeed-backend/internal/redis"

	"github.com/gin-gonic/gin"
)

func (h *Handler) FollowUser(c *gin.Context) {
	userID := c.GetInt("user_id")
	targetUserID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid user ID"})
		return
	}

	if userID == targetUserID {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Cannot follow yourself"})
		return
	}

	// Check if target user exists
	var exists bool
	err = h.db.QueryRow("SELECT EXISTS(SELECT 1 FROM users WHERE id = $1)", targetUserID).Scan(&exists)
	if err != nil || !exists {
		c.JSON(http.StatusNotFound, gin.H{"error": "User not found"})
		return
	}

	// Insert follow relationship (ignore if already exists)
	_, err = h.db.Exec(`
		INSERT INTO follows (follower_id, following_id) 
		VALUES ($1, $2) 
		ON CONFLICT DO NOTHING`,
		userID, targetUserID)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to follow user"})
		return
	}

	// Create notification
	h.createNotification(targetUserID, models.NotificationFollow, userID, nil)

	// Clear cache
	h.redis.Delete(redis.UserCacheKey(userID))
	h.redis.Delete(redis.UserCacheKey(targetUserID))
	h.redis.Delete(redis.FeedCacheKey(userID))

	c.JSON(http.StatusOK, gin.H{"message": "User followed successfully"})
}

func (h *Handler) UnfollowUser(c *gin.Context) {
	userID := c.GetInt("user_id")
	targetUserID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid user ID"})
		return
	}

	_, err = h.db.Exec("DELETE FROM follows WHERE follower_id = $1 AND following_id = $2", 
		userID, targetUserID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to unfollow user"})
		return
	}

	// Clear cache
	h.redis.Delete(redis.UserCacheKey(userID))
	h.redis.Delete(redis.UserCacheKey(targetUserID))
	h.redis.Delete(redis.FeedCacheKey(userID))

	c.JSON(http.StatusOK, gin.H{"message": "User unfollowed successfully"})
}

func (h *Handler) GetFollowers(c *gin.Context) {
	userID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid user ID"})
		return
	}

	currentUserID := c.GetInt("user_id")

	rows, err := h.db.Query(`
		SELECT u.id, u.username, u.email, u.full_name, u.bio, u.avatar, u.is_verified,
		       u.created_at, u.updated_at,
		       EXISTS(SELECT 1 FROM follows WHERE follower_id = $2 AND following_id = u.id) as is_following
		FROM users u
		JOIN follows f ON u.id = f.follower_id
		WHERE f.following_id = $1
		ORDER BY f.created_at DESC`,
		userID, currentUserID)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get followers"})
		return
	}
	defer rows.Close()

	var followers []*models.User
	for rows.Next() {
		var user models.User
		err := rows.Scan(
			&user.ID, &user.Username, &user.Email, &user.FullName, &user.Bio,
			&user.Avatar, &user.IsVerified, &user.CreatedAt, &user.UpdatedAt,
			&user.IsFollowing)

		if err != nil {
			continue
		}

		followers = append(followers, &user)
	}

	c.JSON(http.StatusOK, followers)
}

func (h *Handler) GetFollowing(c *gin.Context) {
	userID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid user ID"})
		return
	}

	currentUserID := c.GetInt("user_id")

	rows, err := h.db.Query(`
		SELECT u.id, u.username, u.email, u.full_name, u.bio, u.avatar, u.is_verified,
		       u.created_at, u.updated_at,
		       EXISTS(SELECT 1 FROM follows WHERE follower_id = $2 AND following_id = u.id) as is_following
		FROM users u
		JOIN follows f ON u.id = f.following_id
		WHERE f.follower_id = $1
		ORDER BY f.created_at DESC`,
		userID, currentUserID)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get following"})
		return
	}
	defer rows.Close()

	var following []*models.User
	for rows.Next() {
		var user models.User
		err := rows.Scan(
			&user.ID, &user.Username, &user.Email, &user.FullName, &user.Bio,
			&user.Avatar, &user.IsVerified, &user.CreatedAt, &user.UpdatedAt,
			&user.IsFollowing)

		if err != nil {
			continue
		}

		following = append(following, &user)
	}

	c.JSON(http.StatusOK, following)
}

func (h *Handler) SearchUsers(c *gin.Context) {
	query := strings.TrimSpace(c.Query("q"))
	if query == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Search query is required"})
		return
	}

	currentUserID := c.GetInt("user_id")

	limit := 20
	if l := c.Query("limit"); l != "" {
		if parsed, err := strconv.Atoi(l); err == nil && parsed > 0 && parsed <= 50 {
			limit = parsed
		}
	}

	searchTerm := "%" + strings.ToLower(query) + "%"

	rows, err := h.db.Query(`
		SELECT u.id, u.username, u.email, u.full_name, u.bio, u.avatar, u.is_verified,
		       u.created_at, u.updated_at,
		       (SELECT COUNT(*) FROM follows WHERE following_id = u.id) as followers_count,
		       (SELECT COUNT(*) FROM follows WHERE follower_id = u.id) as following_count,
		       (SELECT COUNT(*) FROM posts WHERE user_id = u.id) as posts_count,
		       CASE WHEN $2 != u.id THEN 
		           EXISTS(SELECT 1 FROM follows WHERE follower_id = $2 AND following_id = u.id)
		       ELSE false END as is_following
		FROM users u
		WHERE LOWER(u.username) LIKE $1 OR LOWER(u.full_name) LIKE $1
		ORDER BY 
		    CASE WHEN LOWER(u.username) = LOWER($3) THEN 1
		         WHEN LOWER(u.username) LIKE LOWER($3) || '%' THEN 2
		         WHEN LOWER(u.full_name) = LOWER($3) THEN 3
		         WHEN LOWER(u.full_name) LIKE LOWER($3) || '%' THEN 4
		         ELSE 5 END,
		    followers_count DESC
		LIMIT $4`,
		searchTerm, currentUserID, query, limit)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to search users"})
		return
	}
	defer rows.Close()

	var users []*models.User
	for rows.Next() {
		var user models.User
		err := rows.Scan(
			&user.ID, &user.Username, &user.Email, &user.FullName, &user.Bio,
			&user.Avatar, &user.IsVerified, &user.CreatedAt, &user.UpdatedAt,
			&user.FollowersCount, &user.FollowingCount, &user.PostsCount, &user.IsFollowing)

		if err != nil {
			continue
		}

		users = append(users, &user)
	}

	c.JSON(http.StatusOK, users)
}
