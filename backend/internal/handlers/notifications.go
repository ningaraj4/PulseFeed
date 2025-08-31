package handlers

import (
	"net/http"
	"strconv"

	"pulsefeed-backend/internal/models"

	"github.com/gin-gonic/gin"
)

func (h *Handler) GetNotifications(c *gin.Context) {
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

	rows, err := h.db.Query(`
		SELECT n.id, n.user_id, n.type, n.actor_id, n.post_id, n.is_read, n.created_at,
		       u.id, u.username, u.email, u.full_name, u.bio, u.avatar, u.is_verified,
		       u.created_at, u.updated_at,
		       CASE WHEN n.post_id IS NOT NULL THEN
		           (SELECT content FROM posts WHERE id = n.post_id)
		       ELSE NULL END as post_content
		FROM notifications n
		JOIN users u ON n.actor_id = u.id
		WHERE n.user_id = $1
		ORDER BY n.created_at DESC
		LIMIT $2 OFFSET $3`,
		userID, limit, offset)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get notifications"})
		return
	}
	defer rows.Close()

	var notifications []*models.Notification
	for rows.Next() {
		var notification models.Notification
		var actor models.User
		var postContent *string

		err := rows.Scan(
			&notification.ID, &notification.UserID, &notification.Type, &notification.ActorID,
			&notification.PostID, &notification.IsRead, &notification.CreatedAt,
			&actor.ID, &actor.Username, &actor.Email, &actor.FullName, &actor.Bio,
			&actor.Avatar, &actor.IsVerified, &actor.CreatedAt, &actor.UpdatedAt,
			&postContent)

		if err != nil {
			continue
		}

		notification.Actor = &actor
		if postContent != nil && notification.PostID != nil {
			notification.Post = &models.Post{
				ID:      *notification.PostID,
				Content: *postContent,
			}
		}

		notifications = append(notifications, &notification)
	}

	c.JSON(http.StatusOK, notifications)
}

func (h *Handler) MarkNotificationRead(c *gin.Context) {
	userID := c.GetInt("user_id")
	notificationID, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid notification ID"})
		return
	}

	_, err = h.db.Exec(`
		UPDATE notifications 
		SET is_read = true 
		WHERE id = $1 AND user_id = $2`,
		notificationID, userID)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to mark notification as read"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Notification marked as read"})
}
