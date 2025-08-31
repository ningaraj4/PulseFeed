# Development Setup Guide

## Prerequisites

### Required Software
- **Go 1.21+** - [Download](https://golang.org/dl/)
- **Android Studio** - [Download](https://developer.android.com/studio)
- **PostgreSQL 15+** - [Download](https://www.postgresql.org/download/)
- **Redis 7+** - [Download](https://redis.io/download)
- **Git** - [Download](https://git-scm.com/downloads)

### Optional (Recommended)
- **Docker & Docker Compose** - [Download](https://www.docker.com/products/docker-desktop)

## Setup Options

### Option 1: Docker Setup (Recommended)

```bash
# Clone repository
git clone <repository-url>
cd PulseFeed

# Start all services
docker-compose up -d

# Check services status
docker-compose ps

# View logs
docker-compose logs backend
```

### Option 2: Manual Setup

#### 1. Database Setup

**PostgreSQL:**
```bash
# Install PostgreSQL and start service
# Create database
createdb pulsefeed

# Create user (optional)
psql -c "CREATE USER postgres WITH PASSWORD 'password';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE pulsefeed TO postgres;"
```

**Redis:**
```bash
# Install Redis and start service
redis-server

# Test connection
redis-cli ping
```

#### 2. Backend Setup

```bash
cd backend

# Install dependencies
go mod tidy

# Create environment file
cp .env.example .env

# Edit .env with your database credentials
# DATABASE_URL=postgres://postgres:password@localhost:5432/pulsefeed?sslmode=disable
# REDIS_URL=redis://localhost:6379

# Create uploads directory
mkdir uploads

# Build and run
go build -o pulsefeed-backend.exe .
./pulsefeed-backend.exe
```

The backend will:
- Run database migrations automatically
- Insert sample data on first run
- Start on http://localhost:8080

#### 3. Android Setup

```bash
# Open Android Studio
# File -> Open -> Select PulseFeed directory

# Wait for Gradle sync to complete

# Update API base URL if needed
# File: app/src/main/java/com/example/pulsefeed/di/NetworkModule.kt
# Change BASE_URL to your backend URL

# For local development:
# - Emulator: http://10.0.2.2:8080/
# - Physical device: http://YOUR_COMPUTER_IP:8080/

# Run the app
# Run -> Run 'app'
```

## Testing the Application

### Backend API Testing

```bash
# Test health endpoint
curl http://localhost:8080/health

# Test registration
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "full_name": "Test User"
  }'

# Test login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password"
  }'
```

### Sample Test Accounts

The application includes pre-loaded test accounts:

```
Username: john_doe
Password: password

Username: jane_smith
Password: password

Username: bob_wilson
Password: password

Username: alice_brown
Password: password

Username: charlie_davis
Password: password
```

### Android App Testing

1. **Login Flow:**
   - Open app → Login screen
   - Use test credentials above
   - Should navigate to main feed

2. **Feed Testing:**
   - View posts from followed users
   - Like/unlike posts
   - Navigate to comments

3. **Post Creation:**
   - Tap "+" button
   - Create text post
   - Should appear in feed

4. **User Search:**
   - Go to Search tab
   - Search for users by name/username

5. **Profile Management:**
   - View your profile
   - Edit profile information

## Troubleshooting

### Backend Issues

**Database Connection Error:**
```bash
# Check PostgreSQL is running
pg_isready -h localhost -p 5432

# Check database exists
psql -l | grep pulsefeed
```

**Redis Connection Error:**
```bash
# Check Redis is running
redis-cli ping

# Should return "PONG"
```

**Port Already in Use:**
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill process (Windows)
taskkill /PID <process_id> /F
```

### Android Issues

**Network Connection:**
- Emulator: Use `10.0.2.2:8080` for localhost
- Physical device: Use your computer's IP address
- Check firewall settings

**Build Errors:**
```bash
# Clean and rebuild
./gradlew clean build

# Clear Android Studio cache
# File -> Invalidate Caches and Restart
```

**API Connection Issues:**
- Check backend is running on correct port
- Verify BASE_URL in NetworkModule.kt
- Check network permissions in AndroidManifest.xml

### Common Solutions

**CORS Issues:**
- Backend includes CORS middleware
- Check allowed origins in middleware/middleware.go

**JWT Token Issues:**
- Tokens expire after 24 hours
- Use refresh token endpoint
- Clear app data to reset auth state

**Database Migration Issues:**
```bash
# Reset database (WARNING: Deletes all data)
dropdb pulsefeed
createdb pulsefeed

# Restart backend to run migrations
```

## Development Workflow

### Backend Development
```bash
# Install air for hot reload (optional)
go install github.com/cosmtrek/air@latest

# Run with hot reload
air

# Or manual restart
go run main.go
```

### Android Development
```bash
# Run app in debug mode
./gradlew installDebug

# Generate APK
./gradlew assembleDebug

# Run tests
./gradlew test
```

## Production Deployment

### Backend
```bash
# Build for production
go build -ldflags="-s -w" -o pulsefeed-backend .

# Set production environment variables
export JWT_SECRET="your-production-secret"
export DATABASE_URL="your-production-db-url"

# Run
./pulsefeed-backend
```

### Android
```bash
# Generate signed APK
./gradlew assembleRelease

# Or use Android Studio:
# Build -> Generate Signed Bundle/APK
```

## API Documentation

### Authentication Endpoints
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh` - Refresh token

### User Endpoints
- `GET /api/v1/users/me` - Get current user
- `PUT /api/v1/users/me` - Update profile
- `POST /api/v1/users/{id}/follow` - Follow user
- `GET /api/v1/users/search` - Search users

### Post Endpoints
- `GET /api/v1/posts/feed` - Get timeline
- `POST /api/v1/posts` - Create post
- `POST /api/v1/posts/{id}/like` - Like post
- `GET /api/v1/posts/{id}/comments` - Get comments

### WebSocket
- `GET /ws?token={jwt_token}` - Real-time updates

For complete API documentation, see the handlers in `backend/internal/handlers/`

## Support

If you encounter issues:
1. Check this troubleshooting guide
2. Review logs for error messages
3. Ensure all prerequisites are installed
4. Verify environment configuration
