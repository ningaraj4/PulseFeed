# Manual Setup Guide (Without Docker)

Since you already have PostgreSQL and Redis installed, here's how to configure and run your Twitter-like app manually:

## Step 1: Configure PostgreSQL

### Create Database and User
```sql
-- Open PostgreSQL command line (psql) or pgAdmin
-- Connect as superuser (usually 'postgres')

-- Create the database
CREATE DATABASE pulsefeed;

-- Create user with password (if not exists)
CREATE USER postgres WITH PASSWORD 'password';

-- Grant all privileges
GRANT ALL PRIVILEGES ON DATABASE pulsefeed TO postgres;

-- Connect to the new database
\c pulsefeed;

-- Grant schema permissions
GRANT ALL ON SCHEMA public TO postgres;
```

### Verify PostgreSQL Setup
```bash
# Test connection
psql -h localhost -p 5432 -U postgres -d pulsefeed

# Should connect successfully and show:
# pulsefeed=#
```

## Step 2: Configure Redis

### Start Redis Server
```bash
# Method 1: If Redis is installed as service
net start redis

# Method 2: Start Redis manually
redis-server

# Method 3: Start with config file
redis-server redis.conf
```

### Verify Redis Setup
```bash
# Open Redis CLI
redis-cli

# Test connection
ping
# Should return: PONG

# Check Redis is running on default port
redis-cli -p 6379 ping
```

## Step 3: Setup Backend

### Configure Environment
The `.env` file is already created with correct settings:
```env
DATABASE_URL=postgres://postgres:password@localhost:5432/pulsefeed?sslmode=disable
REDIS_URL=redis://localhost:6379
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
PORT=8080
UPLOAD_PATH=./uploads
MAX_UPLOAD_SIZE=10485760
```

### Create Required Directories
```bash
cd d:\Ningaraj\AndroidStudioProjects\PulseFeed\backend
mkdir uploads
```

### Install Dependencies and Run
```bash
# Install Go dependencies
go mod tidy

# Build the application
go build -o pulsefeed-backend.exe .

# Run the backend
./pulsefeed-backend.exe

# OR run directly without building
go run main.go
```

## Step 4: Verify Backend is Running

### Check Backend Startup
When you run the backend, you should see:
```
2024/08/24 22:10:00 Loading environment variables...
2024/08/24 22:10:00 Connecting to database...
2024/08/24 22:10:00 Running database migrations...
2024/08/24 22:10:00 Database migrations completed successfully
2024/08/24 22:10:00 Connecting to Redis...
2024/08/24 22:10:00 Redis connected successfully
2024/08/24 22:10:00 Starting WebSocket hub...
2024/08/24 22:10:00 Server starting on port 8080...
```

### Test API Endpoints
```bash
# Test health endpoint
curl http://localhost:8080/health

# Test login with sample user
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"john_doe\", \"password\": \"password\"}"

# Should return JWT token and user info
```

## Step 5: Run Android App

### Update Network Configuration (if needed)
```kotlin
// File: app/src/main/java/com/example/pulsefeed/di/NetworkModule.kt
// Line 22: Update BASE_URL if needed

private const val BASE_URL = "http://10.0.2.2:8080/" // For Android emulator
// OR
private const val BASE_URL = "http://192.168.1.100:8080/" // For physical device (use your PC's IP)
```

### Run in Android Studio
1. Open Android Studio
2. Open PulseFeed project
3. Wait for Gradle sync
4. Run app on emulator or device
5. Login with test credentials

## Sample Test Data

The backend automatically creates sample data on first run:

### Test Users
```
Username: john_doe     | Password: password
Username: jane_smith   | Password: password
Username: bob_wilson   | Password: password
Username: alice_brown  | Password: password
Username: charlie_davis| Password: password
```

### Sample Content
- 10 posts with various content
- Follow relationships between users
- Likes and comments on posts
- Notifications for interactions

## Troubleshooting

### PostgreSQL Issues

**Connection refused:**
```bash
# Check if PostgreSQL service is running
pg_isready -h localhost -p 5432

# Start PostgreSQL service (Windows)
net start postgresql-x64-15

# Check PostgreSQL status
sc query postgresql-x64-15
```

**Authentication failed:**
```bash
# Reset password for postgres user
psql -U postgres
ALTER USER postgres PASSWORD 'password';

# Or create new user
CREATE USER pulsefeed WITH PASSWORD 'password' CREATEDB;
```

**Database doesn't exist:**
```sql
-- Connect as superuser and create database
psql -U postgres
CREATE DATABASE pulsefeed OWNER postgres;
```

### Redis Issues

**Connection refused:**
```bash
# Check if Redis is running
redis-cli ping

# Start Redis service
net start redis

# Or start manually
redis-server

# Check Redis process
tasklist | findstr redis
```

**Redis not installed as service:**
```bash
# Install Redis as Windows service
redis-server --service-install
redis-server --service-start
```

### Backend Issues

**Database migration errors:**
```bash
# Check database connection
psql -U postgres -d pulsefeed -c "SELECT version();"

# Check if tables exist
psql -U postgres -d pulsefeed -c "\dt"

# Reset database (WARNING: Deletes all data)
psql -U postgres -c "DROP DATABASE IF EXISTS pulsefeed;"
psql -U postgres -c "CREATE DATABASE pulsefeed;"
```

**Port already in use:**
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process
taskkill /PID <process_id> /F

# Or change port in .env file
PORT=8081
```

**Go build errors:**
```bash
# Clean module cache
go clean -modcache

# Reinstall dependencies
go mod tidy
go mod download
```

### Android Issues

**Network connection failed:**
- Ensure backend is running on http://localhost:8080
- For emulator: Use `10.0.2.2:8080`
- For device: Use your computer's IP address
- Check Windows Firewall settings

**Build errors:**
```bash
# Clean and rebuild
./gradlew clean build

# Clear Android Studio caches
# File → Invalidate Caches and Restart
```

## Performance Tips

### PostgreSQL Optimization
```sql
-- Increase shared_buffers (in postgresql.conf)
shared_buffers = 256MB

-- Increase work_mem
work_mem = 4MB

-- Enable query logging (for debugging)
log_statement = 'all'
```

### Redis Optimization
```bash
# Set maxmemory in redis.conf
maxmemory 256mb
maxmemory-policy allkeys-lru
```

### Backend Optimization
```bash
# Build with optimizations
go build -ldflags="-s -w" -o pulsefeed-backend.exe .

# Use air for hot reload during development
go install github.com/cosmtrek/air@latest
air
```

## Development Workflow

### Daily Development
```bash
# 1. Start databases
net start postgresql-x64-15
net start redis

# 2. Start backend
cd backend
go run main.go

# 3. Open Android Studio and run app
```

### Reset Everything
```bash
# Stop services
net stop redis
net stop postgresql-x64-15

# Reset database
psql -U postgres -c "DROP DATABASE pulsefeed;"
psql -U postgres -c "CREATE DATABASE pulsefeed;"

# Restart services
net start postgresql-x64-15
net start redis

# Restart backend (will recreate tables and sample data)
go run main.go
```

This manual setup gives you full control over each component and is perfect for development and learning!
