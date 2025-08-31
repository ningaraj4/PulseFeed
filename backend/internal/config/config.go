package config

import (
	"os"
)

type Config struct {
	DatabaseURL       string
	RedisURL         string
	JWTSecret        string
	GoogleClientID   string
	GoogleClientSecret string
	Port             string
	UploadPath       string
	MaxUploadSize    int64
}

func Load() *Config {
	return &Config{
		DatabaseURL:       getEnv("DATABASE_URL", "postgres://postgres:password@localhost:5432/pulsefeed?sslmode=disable"),
		RedisURL:         getEnv("REDIS_URL", "redis://localhost:6379"),
		JWTSecret:        getEnv("JWT_SECRET", "your-super-secret-jwt-key"),
		GoogleClientID:   getEnv("GOOGLE_CLIENT_ID", ""),
		GoogleClientSecret: getEnv("GOOGLE_CLIENT_SECRET", ""),
		Port:             getEnv("PORT", "8080"),
		UploadPath:       getEnv("UPLOAD_PATH", "./uploads"),
		MaxUploadSize:    10485760, // 10MB
	}
}

func getEnv(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}
