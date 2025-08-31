package com.example.pulsefeed.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("full_name")
    @ColumnInfo(name = "full_name")
    val fullName: String,
    val bio: String,
    val avatar: String,
    @SerializedName("cover_image")
    @ColumnInfo(name = "cover_image")
    val coverImage: String = "",
    @SerializedName("is_verified")
    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean,
    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    val updatedAt: String,
    @SerializedName("followers_count")
    @ColumnInfo(name = "followers_count")
    val followersCount: Int = 0,
    @SerializedName("following_count")
    @ColumnInfo(name = "following_count")
    val followingCount: Int = 0,
    @SerializedName("posts_count")
    @ColumnInfo(name = "posts_count")
    val postsCount: Int = 0,
    @SerializedName("is_following")
    @ColumnInfo(name = "is_following")
    val isFollowing: Boolean = false
)
