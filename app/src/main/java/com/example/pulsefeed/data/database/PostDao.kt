package com.example.pulsefeed.data.database

import androidx.room.*
import com.example.pulsefeed.data.model.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    
    @Query("SELECT * FROM posts ORDER BY created_at DESC")
    fun getAllPostsFlow(): Flow<List<Post>>
    
    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: Int): Post?
    
    @Query("SELECT * FROM posts WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun getPostsByUserId(userId: Int): List<Post>
    
    @Query("SELECT * FROM posts WHERE user_id = :userId ORDER BY created_at DESC")
    fun getPostsByUserIdFlow(userId: Int): Flow<List<Post>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)
    
    @Update
    suspend fun updatePost(post: Post)
    
    @Delete
    suspend fun deletePost(post: Post)
    
    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()
    
    @Query("SELECT * FROM posts WHERE content LIKE '%' || :query || '%' ORDER BY created_at DESC")
    suspend fun searchPosts(query: String): List<Post>
}
