package com.example.pulsefeed.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    onNavigateToPost: (Int) -> Unit = {},
    onNavigateToProfile: (Int) -> Unit = {}
) {
    // Get current user ID (in real app, this would come from auth state)
    val currentUserId = 1
    
    ModernProfileScreen(
        userId = currentUserId,
        onNavigateBack = { /* Handle back navigation if needed */ },
        onNavigateToPost = onNavigateToPost,
        onEditProfile = { /* Handle edit profile */ },
        onFollowClick = { /* Handle follow */ },
        onMessageClick = { /* Handle message */ }
    )
}
