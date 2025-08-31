package com.example.pulsefeed.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey
    val id: Int,
    @SerializedName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: Int,
    val content: String,
    @SerializedName("media_urls")
    @ColumnInfo(name = "media_urls")
    val mediaUrls: List<String> = emptyList(),
    @SerializedName("media_type")
    @ColumnInfo(name = "media_type")
    val mediaType: String = "",
    @SerializedName("likes_count")
    @ColumnInfo(name = "likes_count")
    val likesCount: Int = 0,
    @SerializedName("comments_count")
    @ColumnInfo(name = "comments_count")
    val commentsCount: Int = 0,
    @SerializedName("shares_count")
    @ColumnInfo(name = "shares_count")
    val sharesCount: Int = 0,
    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String,
    @SerializedName("is_liked")
    @ColumnInfo(name = "is_liked")
    val isLiked: Boolean = false
)

data class PostWithUser(
    @Embedded val post: Post,
    @Embedded val user: User?
)
