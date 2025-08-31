package com.example.pulsefeed.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey
    val id: Int,
    @SerializedName("user_id")
    @ColumnInfo(name = "user_id")
    val userId: Int,
    val type: String,
    @SerializedName("actor_id")
    @ColumnInfo(name = "actor_id")
    val actorId: Int,
    @SerializedName("post_id")
    @ColumnInfo(name = "post_id")
    val postId: Int? = null,
    @SerializedName("is_read")
    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,
    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String
)

data class NotificationWithDetails(
    @Embedded val notification: Notification,
    @Embedded val actor: User?,
    @Embedded val post: Post?
)
