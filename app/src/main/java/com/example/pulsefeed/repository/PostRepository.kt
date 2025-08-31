package com.example.pulsefeed.repository

import com.example.pulsefeed.data.api.ApiService
import com.example.pulsefeed.data.database.PostDao
import com.example.pulsefeed.data.model.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val apiService: ApiService,
    private val postDao: PostDao
) {
    
    suspend fun getFeed(limit: Int = 20, offset: Int = 0): Result<FeedResponse> {
        return try {
            val response = apiService.getFeed(limit, offset)
            if (response.isSuccessful && response.body() != null) {
                val feedResponse = response.body()!!
                // Cache posts locally
                val posts = feedResponse.posts.map { it.post }
                postDao.insertPosts(posts)
                Result.success(feedResponse)
            } else {
                Result.failure(Exception("Failed to get feed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createPost(
        content: String,
        mediaUrls: List<String> = emptyList(),
        mediaType: String = ""
    ): Result<PostWithUser> {
        return try {
            val request = CreatePostRequest(content, mediaUrls, mediaType)
            val response = apiService.createPost(request)
            if (response.isSuccessful && response.body() != null) {
                val postWithUser = response.body()!!
                postDao.insertPost(postWithUser.post)
                Result.success(postWithUser)
            } else {
                Result.failure(Exception("Failed to create post: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPost(postId: Int): Result<PostWithUser> {
        return try {
            val response = apiService.getPost(postId)
            if (response.isSuccessful && response.body() != null) {
                val postWithUser = response.body()!!
                postDao.insertPost(postWithUser.post)
                Result.success(postWithUser)
            } else {
                Result.failure(Exception("Failed to get post: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun likePost(postId: Int): Result<String> {
        return try {
            val response = apiService.likePost(postId)
            if (response.isSuccessful) {
                // Update local post like status
                val post = postDao.getPostById(postId)
                post?.let {
                    val updatedPost = it.copy(
                        isLiked = true,
                        likesCount = it.likesCount + 1
                    )
                    postDao.updatePost(updatedPost)
                }
                Result.success("Post liked successfully")
            } else {
                Result.failure(Exception("Failed to like post: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unlikePost(postId: Int): Result<String> {
        return try {
            val response = apiService.unlikePost(postId)
            if (response.isSuccessful) {
                // Update local post like status
                val post = postDao.getPostById(postId)
                post?.let {
                    val updatedPost = it.copy(
                        isLiked = false,
                        likesCount = maxOf(0, it.likesCount - 1)
                    )
                    postDao.updatePost(updatedPost)
                }
                Result.success("Post unliked successfully")
            } else {
                Result.failure(Exception("Failed to unlike post: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getComments(postId: Int): Result<List<CommentWithUser>> {
        return try {
            val response = apiService.getComments(postId)
            if (response.isSuccessful && response.body() != null) {
                val comments = response.body()!!
                Result.success(comments)
            } else {
                Result.failure(Exception("Failed to get comments: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createComment(postId: Int, content: String): Result<CommentWithUser> {
        return try {
            val request = CreateCommentRequest(content)
            val response = apiService.createComment(postId, request)
            if (response.isSuccessful && response.body() != null) {
                val comment = response.body()!!
                // Update post comments count
                val post = postDao.getPostById(postId)
                post?.let {
                    val updatedPost = it.copy(commentsCount = it.commentsCount + 1)
                    postDao.updatePost(updatedPost)
                }
                Result.success(comment)
            } else {
                Result.failure(Exception("Failed to create comment: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserPosts(userId: Int, limit: Int = 20, offset: Int = 0): Result<FeedResponse> {
        return try {
            val response = apiService.getUserPosts(userId, limit, offset)
            if (response.isSuccessful && response.body() != null) {
                val feedResponse = response.body()!!
                // Cache posts locally
                val posts = feedResponse.posts.map { it.post }
                postDao.insertPosts(posts)
                Result.success(feedResponse)
            } else {
                Result.failure(Exception("Failed to get user posts: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadMedia(file: File): Result<UploadResponse> {
        return try {
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("media", file.name, requestFile)
            val response = apiService.uploadMedia(body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to upload media: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getAllPostsFlow(): Flow<List<Post>> {
        return postDao.getAllPostsFlow()
    }
    
    fun getPostsByUserIdFlow(userId: Int): Flow<List<Post>> {
        return postDao.getPostsByUserIdFlow(userId)
    }
}
