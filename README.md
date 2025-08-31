# PulseFeed - Twitter-like Social Media App

A full-stack Twitter-like social media application built with **Android (Kotlin + Jetpack Compose)** frontend and **Go (Gin)** backend, featuring real-time updates, media sharing, and social interactions.

## 🚀 Features

### Android App Features
- **Authentication**: JWT-based login/register with Google OAuth support
- **Feed**: Real-time timeline with posts from followed users
- **Post Creation**: Create text posts with image/video uploads
- **Social Interactions**: Like, comment, follow/unfollow users
- **Search**: Find users and hashtags
- **Profile Management**: View and edit user profiles
- **Notifications**: Real-time notifications for likes, comments, follows
- **Offline Support**: Local caching with Room database

### Backend Features
- **RESTful API**: Complete CRUD operations for users, posts, comments
- **Real-time Updates**: WebSocket support for live notifications
- **Authentication**: JWT tokens with refresh mechanism
- **Media Upload**: File upload with support for images and videos
- **Caching**: Redis integration for performance optimization
- **Database**: PostgreSQL with optimized queries and indexing

## 🏗️ Architecture

### Android (MVVM + Clean Architecture)
```
├── data/
│   ├── api/           # Retrofit API interfaces
│   ├── database/      # Room database (local storage)
│   ├── model/         # Data models
│   └── preferences/   # DataStore preferences
├── di/                # Dependency injection (Hilt)
├── repository/        # Repository pattern implementation
├── ui/
│   ├── screen/        # Compose UI screens
│   ├── viewmodel/     # ViewModels
│   └── navigation/    # Navigation setup
└── utils/             # Utility classes
```

### Backend (Layered Architecture)
```
├── internal/
│   ├── config/        # Configuration management
│   ├── database/      # Database connection & migrations
│   ├── handlers/      # HTTP request handlers
│   ├── middleware/    # Authentication & CORS middleware
│   ├── models/        # Data models
│   ├── redis/         # Redis client & caching
│   └── websocket/     # WebSocket hub for real-time updates
├── uploads/           # Media file storage
└── main.go           # Application entry point
```

## 🛠️ Technology Stack

### Android
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM + Repository Pattern
- **Networking**: Retrofit + OkHttp
- **Local Storage**: Room Database
- **Dependency Injection**: Hilt
- **Image Loading**: Coil
- **Navigation**: Navigation Compose

### Backend
- **Language**: Go 1.21
- **Framework**: Gin
- **Database**: PostgreSQL 15
- **Caching**: Redis 7
- **Authentication**: JWT
- **WebSocket**: Gorilla WebSocket
- **Password Hashing**: bcrypt

### DevOps
- **Containerization**: Docker & Docker Compose
- **Database**: PostgreSQL with automated migrations
- **Caching**: Redis for session and feed caching

## 🚀 Quick Start

### Prerequisites
- **Android Development**: Android Studio, Android SDK (API 24+)
- **Backend Development**: Go 1.21+, Docker & Docker Compose
- **Database**: PostgreSQL 15+ (or use Docker)

### 1. Backend Setup

#### Using Docker (Recommended)
```bash
# Clone the repository
git clone <repository-url>
cd PulseFeed

# Start all services (PostgreSQL, Redis, Backend)
docker-compose up -d

# Check if services are running
docker-compose ps
```

#### Manual Setup
```bash
# Navigate to backend directory
cd backend

# Copy environment file
cp .env.example .env

# Edit .env with your database credentials
# DATABASE_URL=postgres://postgres:password@localhost:5432/pulsefeed?sslmode=disable
# REDIS_URL=redis://localhost:6379
# JWT_SECRET=your-super-secret-jwt-key

# Install dependencies
go mod tidy

# Run database migrations
go run main.go

# Start the server
go run main.go
```

### 2. Android Setup

```bash
# Open Android Studio
# File -> Open -> Select PulseFeed directory

# Sync Gradle files
# Build -> Sync Project with Gradle Files

# Update API base URL in NetworkModule.kt if needed
# Default: http://10.0.2.2:8080/ (Android emulator localhost)

# Run the app
# Run -> Run 'app'
```

### 3. API Endpoints

The backend will be available at `http://localhost:8080`

#### Authentication
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh` - Refresh JWT token
- `POST /api/v1/auth/google` - Google OAuth login

#### Users
- `GET /api/v1/users/me` - Get current user profile
- `PUT /api/v1/users/me` - Update profile
- `GET /api/v1/users/{id}` - Get user profile
- `POST /api/v1/users/{id}/follow` - Follow user
- `DELETE /api/v1/users/{id}/follow` - Unfollow user
- `GET /api/v1/users/search` - Search users

#### Posts
- `GET /api/v1/posts/feed` - Get timeline feed
- `POST /api/v1/posts` - Create new post
- `GET /api/v1/posts/{id}` - Get specific post
- `POST /api/v1/posts/{id}/like` - Like post
- `DELETE /api/v1/posts/{id}/like` - Unlike post
- `GET /api/v1/posts/{id}/comments` - Get post comments
- `POST /api/v1/posts/{id}/comments` - Add comment

#### Media Upload
- `POST /api/v1/uploads/media` - Upload image/video

#### WebSocket
- `GET /ws?token={jwt_token}` - WebSocket connection for real-time updates

## 📱 App Screenshots

### Authentication Flow
- **Login Screen**: Clean login form with validation
- **Register Screen**: User registration with form validation

### Main Features
- **Feed Screen**: Timeline with posts, likes, comments
- **Create Post**: Rich text editor with media upload
- **Profile Screen**: User profile with posts and stats
- **Search Screen**: User search with real-time results
- **Notifications**: Real-time notifications for social interactions

## 🔧 Configuration

### Backend Configuration (.env)
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

### Android Configuration
- **Base URL**: Update in `NetworkModule.kt`
- **API Timeout**: Configure in OkHttp client
- **Database**: Room database auto-migration enabled

## 🧪 Sample Data

The application includes sample data for testing:
- **5 sample users** with different profiles
- **10 sample posts** with various content types
- **Follow relationships** between users
- **Likes and comments** on posts
- **Notifications** for user interactions

### Test Credentials
```
Username: john_doe
Password: password

Username: jane_smith  
Password: password
```

## 🚀 Deployment

### Backend Deployment
```bash
# Build Docker image
docker build -t pulsefeed-backend ./backend

# Run with environment variables
docker run -p 8080:8080 \
  -e DATABASE_URL="your-db-url" \
  -e REDIS_URL="your-redis-url" \
  -e JWT_SECRET="your-jwt-secret" \
  pulsefeed-backend
```

### Android Deployment
```bash
# Generate signed APK
# Build -> Generate Signed Bundle/APK
# Follow Android Studio signing process

# Or build from command line
./gradlew assembleRelease
```

## 🔒 Security Features

- **JWT Authentication** with access/refresh tokens
- **Password Hashing** using bcrypt
- **Input Validation** on all API endpoints
- **CORS Protection** for web security
- **SQL Injection Prevention** with parameterized queries
- **File Upload Validation** with type and size limits

## 📊 Performance Optimizations

### Backend
- **Redis Caching** for frequently accessed data
- **Database Indexing** on commonly queried fields
- **Connection Pooling** for database connections
- **Pagination** for large data sets
- **WebSocket** for real-time updates (reduces polling)

### Android
- **Room Database** for offline data access
- **Image Caching** with Coil
- **Lazy Loading** in RecyclerView/LazyColumn
- **State Management** with ViewModels
- **Network Caching** with OkHttp

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Troubleshooting

### Common Issues

#### Backend Issues
- **Database Connection**: Ensure PostgreSQL is running and credentials are correct
- **Redis Connection**: Verify Redis server is accessible
- **Port Conflicts**: Change port in docker-compose.yml if 8080 is occupied

#### Android Issues
- **Network Connection**: Update base URL for your environment
- **Build Errors**: Run `./gradlew clean build`
- **Emulator Issues**: Use `10.0.2.2` for localhost on Android emulator

#### Docker Issues
```bash
# Reset Docker environment
docker-compose down -v
docker-compose up -d

# View logs
docker-compose logs backend
docker-compose logs postgres
```

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the API documentation

---

**Built with ❤️ using modern Android and Go technologies**
