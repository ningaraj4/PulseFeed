# Docker Installation & Setup Guide

## Why Use Docker?

Docker provides the **easiest and most reliable** way to run your Twitter-like app because:
- ✅ **One-command setup** - Start PostgreSQL, Redis, and backend together
- ✅ **No configuration hassles** - Pre-configured with correct settings
- ✅ **Isolated environment** - Won't conflict with your system
- ✅ **Consistent across machines** - Works the same everywhere
- ✅ **Easy cleanup** - Remove everything with one command

## Docker Installation (Windows)

### Step 1: Download Docker Desktop
1. Go to [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/)
2. Click **"Download for Windows"**
3. Run the installer (`Docker Desktop Installer.exe`)

### Step 2: Installation Process
1. **Run installer as Administrator**
2. **Enable WSL 2** (recommended) when prompted
3. **Restart computer** when installation completes
4. **Start Docker Desktop** from Start menu

### Step 3: Verify Installation
```powershell
# Open PowerShell and run:
docker --version
docker-compose --version

# Should show version numbers like:
# Docker version 24.0.x
# Docker Compose version v2.x.x
```

### Step 4: Configure Docker (Optional)
1. Open Docker Desktop
2. Go to **Settings** → **Resources**
3. Allocate at least **4GB RAM** for better performance
4. Click **Apply & Restart**

## Quick Start with Docker

### 1. Start Your App (One Command!)
```bash
# Navigate to your project
cd d:\Ningaraj\AndroidStudioProjects\PulseFeed

# Start everything (PostgreSQL + Redis + Backend)
docker-compose up -d

# Check if services are running
docker-compose ps
```

### 2. View Logs (If Needed)
```bash
# View all logs
docker-compose logs

# View specific service logs
docker-compose logs backend
docker-compose logs postgres
docker-compose logs redis
```

### 3. Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove all data (clean reset)
docker-compose down -v
```

## Manual Setup (Without Docker)

Since you already have PostgreSQL and Redis installed, here's how to set them up:

### PostgreSQL Setup

#### 1. Create Database
```sql
-- Open PostgreSQL command line (psql)
-- Or use pgAdmin GUI

-- Create database
CREATE DATABASE pulsefeed;

-- Create user (if needed)
CREATE USER postgres WITH PASSWORD 'password';

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE pulsefeed TO postgres;

-- Connect to database
\c pulsefeed;
```

#### 2. Verify Connection
```bash
# Test connection
psql -h localhost -p 5432 -U postgres -d pulsefeed

# Should connect successfully
```

### Redis Setup

#### 1. Start Redis Server
```bash
# Start Redis (usually runs on port 6379)
redis-server

# Or if installed as Windows service:
net start redis
```

#### 2. Test Redis Connection
```bash
# Open Redis CLI
redis-cli

# Test with ping
ping
# Should return: PONG

# Exit
exit
```

### Backend Setup (Manual)

#### 1. Configure Environment
```bash
cd d:\Ningaraj\AndroidStudioProjects\PulseFeed\backend

# The .env file is already created with:
# DATABASE_URL=postgres://postgres:password@localhost:5432/pulsefeed?sslmode=disable
# REDIS_URL=redis://localhost:6379
# JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
# PORT=8080
```

#### 2. Create Uploads Directory
```bash
mkdir uploads
```

#### 3. Run Backend
```bash
# Install dependencies
go mod tidy

# Build and run
go build -o pulsefeed-backend.exe .
./pulsefeed-backend.exe

# Or run directly
go run main.go
```

The backend will:
- ✅ Connect to PostgreSQL and Redis
- ✅ Run database migrations automatically
- ✅ Insert sample data (5 users, 10 posts)
- ✅ Start API server on http://localhost:8080

## Troubleshooting

### Docker Issues

**Docker Desktop won't start:**
```bash
# Enable Hyper-V and WSL 2
# Control Panel → Programs → Turn Windows features on/off
# Enable: Hyper-V, Windows Subsystem for Linux
# Restart computer
```

**Port conflicts:**
```bash
# Check what's using port 5432 (PostgreSQL)
netstat -ano | findstr :5432

# Check what's using port 6379 (Redis)
netstat -ano | findstr :6379

# Kill process if needed
taskkill /PID <process_id> /F
```

### Manual Setup Issues

**PostgreSQL connection failed:**
```bash
# Check if PostgreSQL is running
pg_isready -h localhost -p 5432

# Start PostgreSQL service (Windows)
net start postgresql-x64-15

# Check PostgreSQL logs
# Usually in: C:\Program Files\PostgreSQL\15\data\log\
```

**Redis connection failed:**
```bash
# Check if Redis is running
redis-cli ping

# Start Redis service (Windows)
net start redis

# Or run Redis manually
redis-server
```

**Backend database errors:**
```bash
# Check database exists
psql -l | findstr pulsefeed

# Check user permissions
psql -U postgres -d pulsefeed -c "\du"
```

## Recommended Approach

### For Development: Use Docker 🐳
**Pros:**
- ✅ Fastest setup (2 minutes)
- ✅ No configuration needed
- ✅ Includes sample data
- ✅ Easy to reset/cleanup
- ✅ Production-like environment

**Setup:**
```bash
# Install Docker Desktop (15 minutes one-time)
# Then just run:
docker-compose up -d
```

### For Learning: Manual Setup 🔧
**Pros:**
- ✅ Understand each component
- ✅ Full control over configuration
- ✅ Use existing PostgreSQL/Redis

**Setup:**
```bash
# Configure databases (30 minutes)
# Run backend manually
go run main.go
```

## Testing Your Setup

### 1. Test Backend API
```bash
# Health check
curl http://localhost:8080/health

# Login with sample user
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "john_doe", "password": "password"}'
```

### 2. Test Android App
1. Open Android Studio
2. Open PulseFeed project
3. Run app on emulator/device
4. Login with: `john_doe` / `password`
5. Should see feed with sample posts

## Sample Data Included

Your app comes with pre-loaded test data:

**Users:**
- john_doe / password
- jane_smith / password  
- bob_wilson / password
- alice_brown / password
- charlie_davis / password

**Content:**
- 10 sample posts
- Like/comment interactions
- Follow relationships
- Notifications

## Next Steps

1. **Choose your setup method** (Docker recommended)
2. **Follow the setup steps** above
3. **Test the backend** API endpoints
4. **Run the Android app** in Android Studio
5. **Start developing** your features!

The app is ready to run with either approach. Docker is faster and easier, but manual setup gives you more control.
