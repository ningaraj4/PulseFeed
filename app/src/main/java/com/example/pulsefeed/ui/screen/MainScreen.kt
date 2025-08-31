package com.example.pulsefeed.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToProfile: (Int) -> Unit,
    onNavigateToPost: (Int) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { selectedTab = 2 },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                containerColor = Color(0xFF667eea),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Post",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color(0xFF667eea)
            ) {
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.Default.Home, 
                            contentDescription = "Home",
                            tint = if (selectedTab == 0) Color(0xFF667eea) else Color.Gray
                        ) 
                    },
                    label = { 
                        Text(
                            "Home",
                            color = if (selectedTab == 0) Color(0xFF667eea) else Color.Gray
                        ) 
                    },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.Default.Search, 
                            contentDescription = "Search",
                            tint = if (selectedTab == 1) Color(0xFF667eea) else Color.Gray
                        ) 
                    },
                    label = { 
                        Text(
                            "Search",
                            color = if (selectedTab == 1) Color(0xFF667eea) else Color.Gray
                        ) 
                    },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.Default.Add, 
                            contentDescription = "Create",
                            tint = if (selectedTab == 2) Color(0xFF667eea) else Color.Gray
                        ) 
                    },
                    label = { 
                        Text(
                            "Create",
                            color = if (selectedTab == 2) Color(0xFF667eea) else Color.Gray
                        ) 
                    },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.Default.Notifications, 
                            contentDescription = "Notifications",
                            tint = if (selectedTab == 3) Color(0xFF667eea) else Color.Gray
                        ) 
                    },
                    label = { 
                        Text(
                            "Notifications",
                            color = if (selectedTab == 3) Color(0xFF667eea) else Color.Gray
                        ) 
                    },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { 
                        Icon(
                            Icons.Default.Person, 
                            contentDescription = "Profile",
                            tint = if (selectedTab == 4) Color(0xFF667eea) else Color.Gray
                        ) 
                    },
                    label = { 
                        Text(
                            "Profile",
                            color = if (selectedTab == 4) Color(0xFF667eea) else Color.Gray
                        ) 
                    },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        }
    ) { _ ->
        when (selectedTab) {
            0 -> FeedScreen(
                onNavigateToPost = onNavigateToPost,
                onNavigateToProfile = onNavigateToProfile
            )
            1 -> SearchScreen(
                onNavigateToProfile = onNavigateToProfile
            )
            2 -> CreatePostScreen(
                onNavigateBack = { selectedTab = 0 },
                onPostCreated = { selectedTab = 0 }
            )
            3 -> NotificationsScreen(
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToPost = onNavigateToPost
            )
            4 -> MyProfileScreen(
                onNavigateToPost = onNavigateToPost,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    }
}
