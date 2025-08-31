package com.example.pulsefeed.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey
    val id: Int,
    @SerializedName("post_id")
    @ColumnInfo(name = "post_id")
    val postId: Int,
    @SerializedName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: Int,
    val content: String,
    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String
)

data class CommentWithUser(
    @Embedded val comment: Comment,
    @Embedded val user: User?
)
