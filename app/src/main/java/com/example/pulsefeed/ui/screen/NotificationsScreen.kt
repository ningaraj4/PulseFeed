package com.example.pulsefeed.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NotificationItem(
    val id: Int,
    val type: NotificationType,
    val title: String,
    val content: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val userAvatar: String = "👤",
    val userName: String = "",
    val isVerified: Boolean = false
)

enum class NotificationType {
    LIKE, COMMENT, FOLLOW, MENTION, REPOST, LIVE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateToProfile: (Int) -> Unit = {},
    onNavigateToPost: (Int) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("All") }
    var notifications by remember { mutableStateOf(getSampleNotifications()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    "Notifications",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(onClick = { /* Settings */ }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black
            )
        )
        
        // Tab Row
        TabRow(
            selectedTabIndex = when (selectedTab) {
                "All" -> 0
                "Verified" -> 1
                else -> 2
            },
            containerColor = Color.Black,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.offset(x = tabPositions[when (selectedTab) {
                        "All" -> 0
                        "Verified" -> 1
                        else -> 2
                    }].left),
                    color = Color(0xFF1DA1F2),
                    height = 3.dp
                )
            }
        ) {
            listOf("All", "Verified", "Mentions").forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            tab,
                            color = if (selectedTab == tab) Color.White else Color.Gray,
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }
        
        // Notifications List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(notifications.filter { notification ->
                when (selectedTab) {
                    "Verified" -> notification.isVerified
                    "Mentions" -> notification.type == NotificationType.MENTION
                    else -> true
                }
            }) { notification ->
                NotificationCard(
                    notification = notification,
                    onProfileClick = { onNavigateToProfile(notification.id) },
                    onNotificationClick = { 
                        if (notification.type == NotificationType.LIKE || 
                            notification.type == NotificationType.COMMENT ||
                            notification.type == NotificationType.REPOST) {
                            onNavigateToPost(notification.id)
                        }
                    }
                )
                Divider(
                    color = Color(0xFF2F3336),
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (!notification.isRead) Color(0xFF0A0A0A) else Color.Black)
            .clickable { onNotificationClick() }
            .padding(16.dp)
    ) {
        // Notification icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(getNotificationIconColor(notification.type)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getNotificationIcon(notification.type),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Profile picture (if applicable)
        if (notification.userName.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1DA1F2))
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = notification.userAvatar,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
        }
        
        Column(modifier = Modifier.weight(1f)) {
            // Notification content
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                
                if (notification.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = Color(0xFF1DA1F2),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = notification.content,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = notification.timestamp,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        
        // Unread indicator
        if (!notification.isRead) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1DA1F2))
            )
        }
    }
}

private fun getNotificationIcon(type: NotificationType) = when (type) {
    NotificationType.LIKE -> Icons.Default.Favorite
    NotificationType.COMMENT -> Icons.Default.ChatBubble
    NotificationType.FOLLOW -> Icons.Default.Person
    NotificationType.MENTION -> Icons.Default.AlternateEmail
    NotificationType.REPOST -> Icons.Default.Repeat
    NotificationType.LIVE -> Icons.Default.Videocam
}

private fun getNotificationIconColor(type: NotificationType) = when (type) {
    NotificationType.LIKE -> Color.Red
    NotificationType.COMMENT -> Color(0xFF1DA1F2)
    NotificationType.FOLLOW -> Color(0xFF1DA1F2)
    NotificationType.MENTION -> Color(0xFF1DA1F2)
    NotificationType.REPOST -> Color(0xFF17BF63)
    NotificationType.LIVE -> Color(0xFFE91E63)
}

private fun getSampleNotifications(): List<NotificationItem> = listOf(
    NotificationItem(
        id = 1,
        type = NotificationType.FOLLOW,
        title = "Amisha Aggarwal",
        content = "Ask me anything. No filters. I'll answer",
        timestamp = "2h",
        userAvatar = "👩‍💼",
        userName = "amisha",
        isVerified = true,
        isRead = false
    ),
    NotificationItem(
        id = 2,
        type = NotificationType.LIVE,
        title = "Narendra Modi",
        content = "is LIVE: \"Addressing the press meet with President Ferdinand R. Marcos Jr. of Philippines. @bongbongmarcos\"",
        timestamp = "4h",
        userAvatar = "👨‍💼",
        userName = "narendramodi",
        isVerified = true
    ),
    NotificationItem(
        id = 3,
        type = NotificationType.LIVE,
        title = "Narendra Modi",
        content = "is LIVE: \"आज अपने सभी देश वासियों से चीन-किसान सम्मान निधि की राशि जारी कर और किसानों के लिए विभिन्न विकास कार्यों का उद्घाटन व शिलान्यास कर अत्यंत प्रसन्नता हो रही है।\"",
        timestamp = "6h",
        userAvatar = "👨‍💼",
        userName = "narendramodi",
        isVerified = true
    ),
    NotificationItem(
        id = 4,
        type = NotificationType.LIVE,
        title = "Narendra Modi",
        content = "is LIVE: \"Speaking in the Lok Sabha.\"",
        timestamp = "8h",
        userAvatar = "👨‍💼",
        userName = "narendramodi",
        isVerified = true
    ),
    NotificationItem(
        id = 5,
        type = NotificationType.LIVE,
        title = "Narendra Modi",
        content = "is LIVE: \"Tamil Nadu is witnessing unprecedented development. This growth reflects the Centre's resolve to make the state a driving force of Viksit B...\"",
        timestamp = "12h",
        userAvatar = "👨‍💼",
        userName = "narendramodi",
        isVerified = true
    ),
    NotificationItem(
        id = 6,
        type = NotificationType.LIKE,
        title = "Elon Musk",
        content = "liked your post about Tesla's new Cybertruck",
        timestamp = "1d",
        userAvatar = "👨‍💼",
        userName = "elonmusk",
        isVerified = true,
        isRead = true
    ),
    NotificationItem(
        id = 7,
        type = NotificationType.COMMENT,
        title = "Taylor Swift",
        content = "commented on your post: \"Love this! 💜\"",
        timestamp = "2d",
        userAvatar = "🎤",
        userName = "taylorswift",
        isVerified = true,
        isRead = true
    ),
    NotificationItem(
        id = 8,
        type = NotificationType.REPOST,
        title = "The Rock",
        content = "reposted your workout motivation post",
        timestamp = "3d",
        userAvatar = "💪",
        userName = "therock",
        isVerified = true,
        isRead = true
    ),
    NotificationItem(
        id = 9,
        type = NotificationType.MENTION,
        title = "Cristiano Ronaldo",
        content = "mentioned you in a post about football training",
        timestamp = "4d",
        userAvatar = "⚽",
        userName = "cristiano",
        isVerified = true,
        isRead = true
    ),
    NotificationItem(
        id = 10,
        type = NotificationType.FOLLOW,
        title = "Ariana Grande",
        content = "started following you",
        timestamp = "5d",
        userAvatar = "🎵",
        userName = "arianagrande",
        isVerified = true,
        isRead = true
    )
)
