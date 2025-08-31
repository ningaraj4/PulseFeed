package com.example.pulsefeed.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsefeed.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernProfileScreen(
    userId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToPost: (Int) -> Unit = {},
    onEditProfile: () -> Unit = {},
    onFollowClick: () -> Unit = {},
    onMessageClick: () -> Unit = {}
) {
    // Mock data - replace with real user data
    val user = User(
        id = userId,
        username = "user$userId",
        email = "user$userId@pulsefeed.com",
        fullName = "User $userId",
        bio = "This is a sample bio for user $userId",
        avatar = "",
        coverImage = "",
        isVerified = false,
        createdAt = "2024-01-01T00:00:00Z",
        updatedAt = "2024-01-01T00:00:00Z",
        followersCount = 100,
        followingCount = 50,
        postsCount = 25,
        isFollowing = false
    )
    
    // Simple profile screen without complex animations
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("Profile", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )
        
        // Profile Info
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = user.fullName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "@${user.username}",
                fontSize = 16.sp,
                color = Color.Gray
            )
            
            if (user.bio.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.bio,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row {
                Text(
                    text = "${user.followingCount} Following",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${user.followersCount} Followers",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
        
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
        
        // Empty state for posts
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No posts yet",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

