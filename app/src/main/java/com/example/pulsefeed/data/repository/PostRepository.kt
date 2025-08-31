package com.example.pulsefeed.data.repository

import com.example.pulsefeed.data.model.*
import com.example.pulsefeed.data.api.ApiService
import com.example.pulsefeed.data.model.CreatePostRequest
import com.example.pulsefeed.data.model.FeedResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val apiService: ApiService,
    private val fallbackRepository: FallbackRepository
) {
    
    suspend fun getFeed(page: Int = 1, limit: Int = 20): Result<FeedResponse> {
        return try {
            val response = apiService.getFeed(page, limit)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch feed: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Fallback to sample data when backend is unavailable
            fallbackRepository.getFeedFallback(page, limit)
        }
    }
    
    suspend fun createPost(content: String, imageUrl: String? = null): Result<PostWithUser> {
        return try {
            val request = CreatePostRequest(content, if (imageUrl != null) listOf(imageUrl) else emptyList())
            val response = apiService.createPost(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create post: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Fallback to sample data when backend is unavailable
            // Convert Post to PostWithUser for consistency
            val postResult = fallbackRepository.createPostFallback(content, imageUrl)
            if (postResult.isSuccess) {
                val post = postResult.getOrNull()!!
                // Create a dummy user for fallback
                val user = User(
                    id = 1,
                    username = "fallback_user",
                    email = "fallback@example.com",
                    fullName = "Fallback User",
                    bio = "Fallback account",
                    avatar = "👤",
                    coverImage = "",
                    isVerified = false,
                    createdAt = "2023-01-01",
                    updatedAt = "2023-01-01"
                )
                Result.success(PostWithUser(post, user))
            } else {
                Result.failure(postResult.exceptionOrNull()!!)
            }
        }
    }
    
    suspend fun getPost(postId: Int): Result<PostWithUser> {
        return try {
            val response = apiService.getPost(postId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch post: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun likePost(postId: Int): Result<Boolean> {
        return try {
            val response = apiService.likePost(postId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(true) // Like successful
            } else {
                Result.failure(Exception("Failed to like post: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Fallback for like action
            val fallbackResult = fallbackRepository.likePostFallback(postId)
            fallbackResult
        }
    }
    
    suspend fun unlikePost(postId: Int): Result<Boolean> {
        return try {
            val response = apiService.unlikePost(postId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(false) // Unlike successful
            } else {
                Result.failure(Exception("Failed to unlike post: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Fallback for unlike action
            Result.success(false)
        }
    }
    
    suspend fun getUserPosts(userId: Int): Result<List<PostWithUser>> {
        return try {
            val response = apiService.getUserPosts(userId)
            if (response.isSuccessful && response.body() != null) {
                val feedResponse = response.body()!!
                Result.success(feedResponse.posts)
            } else {
                Result.failure(Exception("Failed to fetch user posts: ${response.message()}"))
            }
        } catch (e: Exception) {
            try {
                val fallbackResult = fallbackRepository.getFeedFallback(1, 20)
                if (fallbackResult.isSuccess) {
                    val feedResponse = fallbackResult.getOrNull()!!
                    Result.success(feedResponse.posts)
                } else {
                    Result.failure(e)
                }
            } catch (fallbackException: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getComments(postId: Int): Result<List<Comment>> {
        return try {
            val response = apiService.getComments(postId)
            if (response.isSuccessful && response.body() != null) {
                val commentsWithUser = response.body()!!
                val comments = commentsWithUser.map { it.comment }
                Result.success(comments)
            } else {
                Result.failure(Exception("Failed to fetch comments: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.success(emptyList<Comment>())
        }
    }
}
