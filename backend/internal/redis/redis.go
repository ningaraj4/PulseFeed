package redis

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/go-redis/redis/v8"
)

type Client struct {
	rdb *redis.Client
	ctx context.Context
}

func Connect(redisURL string) *Client {
	opt, err := redis.ParseURL(redisURL)
	if err != nil {
		// Fallback to default connection
		opt = &redis.Options{
			Addr: "localhost:6379",
		}
	}

	rdb := redis.NewClient(opt)
	ctx := context.Background()

	return &Client{
		rdb: rdb,
		ctx: ctx,
	}
}

func (c *Client) Close() error {
	return c.rdb.Close()
}

func (c *Client) Set(key string, value interface{}, expiration time.Duration) error {
	data, err := json.Marshal(value)
	if err != nil {
		return fmt.Errorf("failed to marshal value: %w", err)
	}

	return c.rdb.Set(c.ctx, key, data, expiration).Err()
}

func (c *Client) Get(key string, dest interface{}) error {
	val, err := c.rdb.Get(c.ctx, key).Result()
	if err != nil {
		return err
	}

	return json.Unmarshal([]byte(val), dest)
}

func (c *Client) Delete(key string) error {
	return c.rdb.Del(c.ctx, key).Err()
}

func (c *Client) Exists(key string) bool {
	result := c.rdb.Exists(c.ctx, key)
	return result.Val() > 0
}

func (c *Client) SetNX(key string, value interface{}, expiration time.Duration) (bool, error) {
	data, err := json.Marshal(value)
	if err != nil {
		return false, fmt.Errorf("failed to marshal value: %w", err)
	}

	return c.rdb.SetNX(c.ctx, key, data, expiration).Result()
}

// Cache keys
func FeedCacheKey(userID int) string {
	return fmt.Sprintf("feed:%d", userID)
}

func UserCacheKey(userID int) string {
	return fmt.Sprintf("user:%d", userID)
}

func PostCacheKey(postID int) string {
	return fmt.Sprintf("post:%d", postID)
}
