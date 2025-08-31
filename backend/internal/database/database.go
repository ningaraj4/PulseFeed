package database

import (
	"database/sql"
	"fmt"
	
	_ "github.com/lib/pq"
)

func Connect(databaseURL string) (*sql.DB, error) {
	db, err := sql.Open("postgres", databaseURL)
	if err != nil {
		return nil, fmt.Errorf("failed to open database: %w", err)
	}

	if err := db.Ping(); err != nil {
		return nil, fmt.Errorf("failed to ping database: %w", err)
	}

	return db, nil
}

func Migrate(db *sql.DB) error {
	queries := []string{
		`CREATE TABLE IF NOT EXISTS users (
			id SERIAL PRIMARY KEY,
			username VARCHAR(50) UNIQUE NOT NULL,
			email VARCHAR(255) UNIQUE NOT NULL,
			password_hash VARCHAR(255) NOT NULL,
			full_name VARCHAR(100) NOT NULL,
			bio TEXT DEFAULT '',
			avatar TEXT DEFAULT '',
			is_verified BOOLEAN DEFAULT FALSE,
			created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
			updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
		)`,
		
		`CREATE TABLE IF NOT EXISTS posts (
			id SERIAL PRIMARY KEY,
			user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
			content TEXT NOT NULL,
			media_urls JSONB DEFAULT '[]',
			media_type VARCHAR(20) DEFAULT '',
			likes_count INTEGER DEFAULT 0,
			comments_count INTEGER DEFAULT 0,
			created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
			updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
		)`,
		
		`CREATE TABLE IF NOT EXISTS comments (
			id SERIAL PRIMARY KEY,
			post_id INTEGER REFERENCES posts(id) ON DELETE CASCADE,
			user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
			content TEXT NOT NULL,
			created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
			updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
		)`,
		
		`CREATE TABLE IF NOT EXISTS likes (
			id SERIAL PRIMARY KEY,
			post_id INTEGER REFERENCES posts(id) ON DELETE CASCADE,
			user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
			created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
			UNIQUE(post_id, user_id)
		)`,
		
		`CREATE TABLE IF NOT EXISTS follows (
			id SERIAL PRIMARY KEY,
			follower_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
			following_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
			created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
			UNIQUE(follower_id, following_id),
			CHECK(follower_id != following_id)
		)`,
		
		`CREATE TABLE IF NOT EXISTS notifications (
			id SERIAL PRIMARY KEY,
			user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
			type VARCHAR(20) NOT NULL,
			actor_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
			post_id INTEGER REFERENCES posts(id) ON DELETE CASCADE,
			is_read BOOLEAN DEFAULT FALSE,
			created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
		)`,
		
		// Indexes for performance
		`CREATE INDEX IF NOT EXISTS idx_posts_user_id ON posts(user_id)`,
		`CREATE INDEX IF NOT EXISTS idx_posts_created_at ON posts(created_at DESC)`,
		`CREATE INDEX IF NOT EXISTS idx_comments_post_id ON comments(post_id)`,
		`CREATE INDEX IF NOT EXISTS idx_likes_post_id ON likes(post_id)`,
		`CREATE INDEX IF NOT EXISTS idx_likes_user_id ON likes(user_id)`,
		`CREATE INDEX IF NOT EXISTS idx_follows_follower_id ON follows(follower_id)`,
		`CREATE INDEX IF NOT EXISTS idx_follows_following_id ON follows(following_id)`,
		`CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id)`,
		`CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at DESC)`,
		
		// Triggers for updating counts
		`CREATE OR REPLACE FUNCTION update_likes_count()
		RETURNS TRIGGER AS $$
		BEGIN
			IF TG_OP = 'INSERT' THEN
				UPDATE posts SET likes_count = likes_count + 1 WHERE id = NEW.post_id;
				RETURN NEW;
			ELSIF TG_OP = 'DELETE' THEN
				UPDATE posts SET likes_count = likes_count - 1 WHERE id = OLD.post_id;
				RETURN OLD;
			END IF;
			RETURN NULL;
		END;
		$$ LANGUAGE plpgsql`,
		
		`CREATE OR REPLACE FUNCTION update_comments_count()
		RETURNS TRIGGER AS $$
		BEGIN
			IF TG_OP = 'INSERT' THEN
				UPDATE posts SET comments_count = comments_count + 1 WHERE id = NEW.post_id;
				RETURN NEW;
			ELSIF TG_OP = 'DELETE' THEN
				UPDATE posts SET comments_count = comments_count - 1 WHERE id = OLD.post_id;
				RETURN OLD;
			END IF;
			RETURN NULL;
		END;
		$$ LANGUAGE plpgsql`,
		
		`DROP TRIGGER IF EXISTS trigger_likes_count ON likes`,
		`CREATE TRIGGER trigger_likes_count
		AFTER INSERT OR DELETE ON likes
		FOR EACH ROW EXECUTE FUNCTION update_likes_count()`,
		
		`DROP TRIGGER IF EXISTS trigger_comments_count ON comments`,
		`CREATE TRIGGER trigger_comments_count
		AFTER INSERT OR DELETE ON comments
		FOR EACH ROW EXECUTE FUNCTION update_comments_count()`,
	}

	for _, query := range queries {
		if _, err := db.Exec(query); err != nil {
			return fmt.Errorf("failed to execute migration: %s, error: %w", query, err)
		}
	}

	return nil
}
