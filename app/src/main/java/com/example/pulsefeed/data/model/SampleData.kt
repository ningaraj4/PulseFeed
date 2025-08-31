package com.example.pulsefeed.data.model

// Simple data classes for sample content (not Room entities)
data class SamplePost(
    val id: Int,
    val userId: Int,
    val username: String,
    val userAvatar: String,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: String,
    val likes: Int,
    val comments: Int,
    val isLiked: Boolean = false
)

data class SampleUser(
    val id: Int,
    val username: String,
    val fullName: String,
    val avatar: String,
    val bio: String,
    val followers: Int,
    val following: Int,
    val posts: Int,
    val isVerified: Boolean = false
)

object SampleData {
    val sampleUsers = listOf(
        SampleUser(
            id = 1,
            username = "you",
            fullName = "Your Name",
            avatar = "👤",
            bio = "Welcome to PulseFeed! Share your thoughts with the world.",
            followers = 0,
            following = 0,
            posts = 0,
            isVerified = false
        )
    )

    val samplePosts = listOf(
        SamplePost(
            id = 1,
            userId = 1,
            username = "you",
            userAvatar = "👤",
            content = "Welcome to PulseFeed! This is your first post. Share your thoughts and connect with friends! 🚀",
            imageUrl = null,
            timestamp = "now",
            likes = 0,
            comments = 0
        )
    )
}
