package com.example.pulsefeed.data.network

import com.example.pulsefeed.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Auth endpoints
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>
    
    @POST("api/v1/auth/google")
    suspend fun googleAuth(@Body request: GoogleAuthRequest): Response<AuthResponse>
    
    @POST("api/v1/auth/send-otp")
    suspend fun sendOTP(@Body request: SendOTPRequest): Response<Unit>
    
    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOTP(@Body request: VerifyOTPRequest): Response<AuthResponse>
    
    // User endpoints
    @GET("api/v1/users/me")
    suspend fun getProfile(): Response<User>
    
    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<User>
    
    @GET("api/v1/users/{id}")
    suspend fun getUserProfile(@Path("id") userId: Int): Response<User>
    
    @POST("api/v1/users/{id}/follow")
    suspend fun followUser(@Path("id") userId: Int): Response<FollowResponse>
    
    @DELETE("api/v1/users/{id}/follow")
    suspend fun unfollowUser(@Path("id") userId: Int): Response<FollowResponse>
    
    @GET("api/v1/users/{id}/followers")
    suspend fun getFollowers(@Path("id") userId: Int): Response<List<User>>
    
    @GET("api/v1/users/{id}/following")
    suspend fun getFollowing(@Path("id") userId: Int): Response<List<User>>
    
    @GET("api/v1/users/search")
    suspend fun searchUsers(@Query("q") query: String): Response<List<User>>
    
    // Post endpoints
    @POST("api/v1/posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<Post>
    
    @GET("api/v1/posts/feed")
    suspend fun getFeed(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<FeedResponse>
    
    @GET("api/v1/posts/{id}")
    suspend fun getPost(@Path("id") postId: Int): Response<PostWithUser>
    
    @POST("api/v1/posts/{id}/like")
    suspend fun likePost(@Path("id") postId: Int): Response<LikeResponse>
    
    @DELETE("api/v1/posts/{id}/like")
    suspend fun unlikePost(@Path("id") postId: Int): Response<LikeResponse>
    
    @GET("api/v1/posts/{id}/comments")
    suspend fun getComments(@Path("id") postId: Int): Response<List<Comment>>
    
    @POST("api/v1/posts/{id}/comments")
    suspend fun createComment(
        @Path("id") postId: Int,
        @Body request: CreateCommentRequest
    ): Response<Comment>
    
    @GET("api/v1/posts/user/{id}")
    suspend fun getUserPosts(@Path("id") userId: Int): Response<List<PostWithUser>>
    
    // Notification endpoints
    @GET("api/v1/notifications")
    suspend fun getNotifications(): Response<List<Notification>>
    
    @PUT("api/v1/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") notificationId: Int): Response<Unit>
    
    // Upload endpoints
    @Multipart
    @POST("api/v1/uploads/media")
    suspend fun uploadMedia(@Part("file") file: okhttp3.MultipartBody.Part): Response<UploadResponse>
}

// Request/Response models
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val displayName: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class GoogleAuthRequest(
    val idToken: String
)

data class AuthResponse(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)

data class UpdateProfileRequest(
    val displayName: String?,
    val bio: String?,
    val profilePicture: String?,
    val coverPhoto: String?
)

data class FollowResponse(
    val isFollowing: Boolean,
    val followersCount: Int
)

data class CreatePostRequest(
    val content: String,
    val imageUrl: String? = null
)

data class FeedResponse(
    val posts: List<PostWithUser>,
    val hasMore: Boolean,
    val nextPage: Int?
)

data class LikeResponse(
    val isLiked: Boolean,
    val likesCount: Int
)

data class CreateCommentRequest(
    val content: String
)

data class UploadResponse(
    val url: String,
    val filename: String
)
