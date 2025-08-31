package com.example.pulsefeed.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("full_name")
    val fullName: String
)

data class AuthResponse(
    val user: User,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)

data class CreatePostRequest(
    val content: String,
    @SerializedName("media_urls")
    val mediaUrls: List<String> = emptyList(),
    @SerializedName("media_type")
    val mediaType: String = ""
)

data class CreateCommentRequest(
    val content: String
)

data class UpdateProfileRequest(
    @SerializedName("full_name")
    val fullName: String? = null,
    val bio: String? = null,
    val avatar: String? = null
)

data class FeedResponse(
    val posts: List<PostWithUser>,
    @SerializedName("next_cursor")
    val nextCursor: String? = null,
    @SerializedName("has_more")
    val hasMore: Boolean = false
)

data class UploadResponse(
    val url: String,
    val type: String
)
