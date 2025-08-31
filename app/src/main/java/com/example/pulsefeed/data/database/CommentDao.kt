package com.example.pulsefeed.data.database

import androidx.room.*
import com.example.pulsefeed.data.model.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    
    @Query("SELECT * FROM comments WHERE post_id = :postId ORDER BY created_at ASC")
    suspend fun getCommentsByPostId(postId: Int): List<Comment>
    
    @Query("SELECT * FROM comments WHERE post_id = :postId ORDER BY created_at ASC")
    fun getCommentsByPostIdFlow(postId: Int): Flow<List<Comment>>
    
    @Query("SELECT * FROM comments WHERE id = :commentId")
    suspend fun getCommentById(commentId: Int): Comment?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<Comment>)
    
    @Update
    suspend fun updateComment(comment: Comment)
    
    @Delete
    suspend fun deleteComment(comment: Comment)
    
    @Query("DELETE FROM comments WHERE post_id = :postId")
    suspend fun deleteCommentsByPostId(postId: Int)
    
    @Query("DELETE FROM comments")
    suspend fun deleteAllComments()
}
