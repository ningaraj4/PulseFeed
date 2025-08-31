package com.example.pulsefeed.data.api

import com.example.pulsefeed.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Auth endpoints
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(): Response<AuthResponse>
    
    @POST("api/v1/auth/google")
    suspend fun googleAuth(@Body token: Map<String, String>): Response<AuthResponse>
    
    // User endpoints
    @GET("api/v1/users/me")
    suspend fun getProfile(): Response<User>
    
    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<User>
    
    @GET("api/v1/users/{id}")
    suspend fun getUserProfile(@Path("id") userId: Int): Response<User>
    
    @POST("api/v1/users/{id}/follow")
    suspend fun followUser(@Path("id") userId: Int): Response<Map<String, String>>
    
    @DELETE("api/v1/users/{id}/follow")
    suspend fun unfollowUser(@Path("id") userId: Int): Response<Map<String, String>>
    
    @GET("api/v1/users/{id}/followers")
    suspend fun getFollowers(@Path("id") userId: Int): Response<List<User>>
    
    @GET("api/v1/users/{id}/following")
    suspend fun getFollowing(@Path("id") userId: Int): Response<List<User>>
    
    @GET("api/v1/users/search")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): Response<List<User>>
    
    // Post endpoints
    @POST("api/v1/posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<PostWithUser>
    
    @GET("api/v1/posts/feed")
    suspend fun getFeed(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<FeedResponse>
    
    @GET("api/v1/posts/{id}")
    suspend fun getPost(@Path("id") postId: Int): Response<PostWithUser>
    
    @POST("api/v1/posts/{id}/like")
    suspend fun likePost(@Path("id") postId: Int): Response<Map<String, String>>
    
    @DELETE("api/v1/posts/{id}/like")
    suspend fun unlikePost(@Path("id") postId: Int): Response<Map<String, String>>
    
    @GET("api/v1/posts/{id}/comments")
    suspend fun getComments(@Path("id") postId: Int): Response<List<CommentWithUser>>
    
    @POST("api/v1/posts/{id}/comments")
    suspend fun createComment(
        @Path("id") postId: Int,
        @Body request: CreateCommentRequest
    ): Response<CommentWithUser>
    
    @GET("api/v1/posts/user/{id}")
    suspend fun getUserPosts(
        @Path("id") userId: Int,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<FeedResponse>
    
    // Notification endpoints
    @GET("api/v1/notifications")
    suspend fun getNotifications(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<List<NotificationWithDetails>>
    
    @PUT("api/v1/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") notificationId: Int): Response<Map<String, String>>
    
    // Upload endpoints
    @Multipart
    @POST("api/v1/uploads/media")
    suspend fun uploadMedia(@Part media: MultipartBody.Part): Response<UploadResponse>
}
