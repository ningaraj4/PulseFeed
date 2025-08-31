package com.example.pulsefeed.data.repository

import com.example.pulsefeed.data.model.*
import com.example.pulsefeed.data.model.FeedResponse
import com.example.pulsefeed.data.model.AuthResponse
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FallbackRepository @Inject constructor() {
    
    // Simulate network delay for realistic testing
    private suspend fun simulateNetworkDelay() {
        delay(Random.nextLong(500, 1500))
    }
    
    suspend fun getFeedFallback(page: Int = 1, limit: Int = 20): Result<FeedResponse> {
        return try {
            simulateNetworkDelay()
            
            // Convert sample data to API format
            val samplePosts = com.example.pulsefeed.data.model.SampleData.samplePosts
            val sampleUsers = com.example.pulsefeed.data.model.SampleData.sampleUsers
            
            val startIndex = (page - 1) * limit
            val endIndex = minOf(startIndex + limit, samplePosts.size)
            
            if (startIndex >= samplePosts.size) {
                return Result.success(FeedResponse(emptyList(), null))
            }
            
            val postsForPage = samplePosts.subList(startIndex, endIndex)
            val postsWithUsers = postsForPage.map { samplePost ->
                val user = sampleUsers.find { it.id == samplePost.userId } ?: sampleUsers.first()
                
                PostWithUser(
                    post = Post(
                        id = samplePost.id,
                        userId = samplePost.userId,
                        content = samplePost.content,
                        mediaUrls = listOfNotNull(samplePost.imageUrl),
                        mediaType = if (samplePost.imageUrl != null) "image" else "",
                        likesCount = samplePost.likes,
                        commentsCount = samplePost.comments,
                        sharesCount = maxOf(0, samplePost.likes / 10),
                        isLiked = samplePost.isLiked,
                        createdAt = samplePost.timestamp,
                        updatedAt = samplePost.timestamp
                    ),
                    user = User(
                        id = user.id,
                        username = user.username,
                        email = "${user.username}@example.com",
                        fullName = user.fullName,
                        bio = user.bio,
                        avatar = user.avatar,
                        followersCount = user.followers,
                        followingCount = user.following,
                        isVerified = user.isVerified,
                        createdAt = "2023-01-01T00:00:00Z",
                        updatedAt = "2023-01-01T00:00:00Z"
                    )
                )
            }
            
            val hasMore = endIndex < samplePosts.size
            val nextPage = if (hasMore) page + 1 else null
            
            Result.success(FeedResponse(postsWithUsers, if (hasMore) nextPage.toString() else null))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createPostFallback(content: String, imageUrl: String? = null): Result<Post> {
        return try {
            simulateNetworkDelay()
            
            val i = Random.nextInt(1, 100)
            val newPost = Post(
                id = Random.nextInt(1000, 9999),
                userId = 1, // Current user
                content = content,
                mediaUrls = if (imageUrl != null) listOf(imageUrl) else emptyList(),
                mediaType = if (imageUrl != null) "image" else "",
                likesCount = Random.nextInt(0, 1000),
                commentsCount = Random.nextInt(0, 100),
                sharesCount = Random.nextInt(0, 50),
                isLiked = false,
                createdAt = "just now",
                updatedAt = "just now"
            )
            
            Result.success(newPost)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun likePostFallback(postId: Int): Result<Boolean> {
        return try {
            simulateNetworkDelay()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun loginFallback(email: String, password: String): Result<AuthResponse> {
        return try {
            simulateNetworkDelay()
            
            // Simulate login validation
            if (email.isBlank() || password.isBlank()) {
                return Result.failure(Exception("Email and password are required"))
            }
            
            val user = User(
                id = 1,
                username = "testuser",
                email = email,
                fullName = "Test User",
                bio = "This is a test user account",
                avatar = "👤",
                followersCount = 1250,
                followingCount = 890,
                isVerified = true,
                createdAt = "2023-01-01T00:00:00Z",
                updatedAt = "2023-01-01T00:00:00Z"
            )
            
            val authResponse = AuthResponse(
                user = user,
                accessToken = "fake-jwt-token-${System.currentTimeMillis()}",
                refreshToken = "fake-refresh-token-${System.currentTimeMillis()}"
            )
            
            Result.success(authResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

