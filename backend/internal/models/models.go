package models

import (
	"time"
	"database/sql/driver"
	"encoding/json"
	"errors"
)

type User struct {
	ID          int       `json:"id" db:"id"`
	Username    string    `json:"username" db:"username"`
	Email       string    `json:"email" db:"email"`
	Password    string    `json:"-" db:"password_hash"`
	FullName    string    `json:"full_name" db:"full_name"`
	Bio         string    `json:"bio" db:"bio"`
	Avatar      string    `json:"avatar" db:"avatar"`
	IsVerified  bool      `json:"is_verified" db:"is_verified"`
	CreatedAt   time.Time `json:"created_at" db:"created_at"`
	UpdatedAt   time.Time `json:"updated_at" db:"updated_at"`
	
	// Computed fields
	FollowersCount int  `json:"followers_count,omitempty"`
	FollowingCount int  `json:"following_count,omitempty"`
	PostsCount     int  `json:"posts_count,omitempty"`
	IsFollowing    bool `json:"is_following,omitempty"`
}

type Post struct {
	ID          int       `json:"id" db:"id"`
	UserID      int       `json:"user_id" db:"user_id"`
	Content     string    `json:"content" db:"content"`
	MediaURLs   MediaURLs `json:"media_urls" db:"media_urls"`
	MediaType   string    `json:"media_type" db:"media_type"`
	LikesCount  int       `json:"likes_count" db:"likes_count"`
	CommentsCount int     `json:"comments_count" db:"comments_count"`
	CreatedAt   time.Time `json:"created_at" db:"created_at"`
	UpdatedAt   time.Time `json:"updated_at" db:"updated_at"`
	
	// Joined fields
	User      *User `json:"user,omitempty"`
	IsLiked   bool  `json:"is_liked,omitempty"`
}

type MediaURLs []string

func (m *MediaURLs) Scan(value interface{}) error {
	if value == nil {
		*m = MediaURLs{}
		return nil
	}
	
	switch v := value.(type) {
	case []byte:
		return json.Unmarshal(v, m)
	case string:
		return json.Unmarshal([]byte(v), m)
	default:
		return errors.New("cannot scan into MediaURLs")
	}
}

func (m MediaURLs) Value() (driver.Value, error) {
	if len(m) == 0 {
		return "[]", nil
	}
	return json.Marshal(m)
}

type Comment struct {
	ID        int       `json:"id" db:"id"`
	PostID    int       `json:"post_id" db:"post_id"`
	UserID    int       `json:"user_id" db:"user_id"`
	Content   string    `json:"content" db:"content"`
	CreatedAt time.Time `json:"created_at" db:"created_at"`
	UpdatedAt time.Time `json:"updated_at" db:"updated_at"`
	
	// Joined fields
	User *User `json:"user,omitempty"`
}

type Like struct {
	ID        int       `json:"id" db:"id"`
	PostID    int       `json:"post_id" db:"post_id"`
	UserID    int       `json:"user_id" db:"user_id"`
	CreatedAt time.Time `json:"created_at" db:"created_at"`
}

type Follow struct {
	ID          int       `json:"id" db:"id"`
	FollowerID  int       `json:"follower_id" db:"follower_id"`
	FollowingID int       `json:"following_id" db:"following_id"`
	CreatedAt   time.Time `json:"created_at" db:"created_at"`
}

type Notification struct {
	ID        int              `json:"id" db:"id"`
	UserID    int              `json:"user_id" db:"user_id"`
	Type      NotificationType `json:"type" db:"type"`
	ActorID   int              `json:"actor_id" db:"actor_id"`
	PostID    *int             `json:"post_id,omitempty" db:"post_id"`
	IsRead    bool             `json:"is_read" db:"is_read"`
	CreatedAt time.Time        `json:"created_at" db:"created_at"`
	
	// Joined fields
	Actor *User `json:"actor,omitempty"`
	Post  *Post `json:"post,omitempty"`
}

type NotificationType string

const (
	NotificationLike    NotificationType = "like"
	NotificationComment NotificationType = "comment"
	NotificationFollow  NotificationType = "follow"
)

// Request/Response models
type RegisterRequest struct {
	Username string `json:"username" binding:"required,min=3,max=50"`
	Email    string `json:"email" binding:"required,email"`
	Password string `json:"password" binding:"required,min=6"`
	FullName string `json:"full_name" binding:"required,min=1,max=100"`
}

type LoginRequest struct {
	Username string `json:"username" binding:"required"`
	Password string `json:"password" binding:"required"`
}

type AuthResponse struct {
	User         *User  `json:"user"`
	AccessToken  string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
}

type CreatePostRequest struct {
	Content   string   `json:"content" binding:"required,max=280"`
	MediaURLs []string `json:"media_urls,omitempty"`
	MediaType string   `json:"media_type,omitempty"`
}

type CreateCommentRequest struct {
	Content string `json:"content" binding:"required,max=280"`
}

type UpdateProfileRequest struct {
	FullName string `json:"full_name,omitempty"`
	Bio      string `json:"bio,omitempty"`
	Avatar   string `json:"avatar,omitempty"`
}

type FeedResponse struct {
	Posts      []*Post `json:"posts"`
	NextCursor string  `json:"next_cursor,omitempty"`
	HasMore    bool    `json:"has_more"`
}
