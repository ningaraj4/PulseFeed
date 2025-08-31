package com.example.pulsefeed.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pulsefeed.data.model.Post
import com.example.pulsefeed.data.model.User
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RealPostCard(
    post: Post,
    user: User,
    onPostClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLikeClick: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(post.isLiked) }
    var likesCount by remember { mutableStateOf(post.likesCount) }
    
    val likeScale by animateFloatAsState(
        targetValue = if (isLiked) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "like_scale"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPostClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Black
        ),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile Picture
                AsyncImage(
                    model = user.avatar.ifEmpty { null },
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() },
                    contentScale = ContentScale.Crop,
                    fallback = androidx.compose.ui.graphics.painter.ColorPainter(Color(0xFF1DA1F2))
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    // User Info Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onProfileClick() }
                    ) {
                        Text(
                            text = user.fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        if (user.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = Color(0xFF1DA1F2),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "@${user.username}",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "·",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatTimeAgo(post.createdAt),
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Post Content
                    Text(
                        text = post.content,
                        fontSize = 15.sp,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                    
                    // Media Content
                    if (post.mediaUrls.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        AsyncImage(
                            model = post.mediaUrls.first(),
                            contentDescription = "Post media",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Engagement Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Reply Button
                        EngagementButton(
                            icon = Icons.Outlined.ChatBubbleOutline,
                            count = post.commentsCount,
                            onClick = { /* Handle reply */ }
                        )
                        
                        // Repost Button
                        EngagementButton(
                            icon = Icons.Outlined.Repeat,
                            count = 0, // post.repostsCount not available in Post model
                            onClick = { /* Handle repost */ }
                        )
                        
                        // Like Button
                        EngagementButton(
                            icon = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            count = likesCount,
                            tint = if (isLiked) Color.Red else Color.Gray,
                            onClick = {
                                val newLikedState = !isLiked
                                isLiked = newLikedState
                                likesCount = if (newLikedState) likesCount + 1 else likesCount - 1
                                onLikeClick(post.id, newLikedState)
                            },
                            modifier = Modifier.scale(likeScale)
                        )
                        
                        // Share Button
                        EngagementButton(
                            icon = Icons.Outlined.Share,
                            count = null,
                            onClick = { /* Handle share */ }
                        )
                    }
                }
                
                // More Options
                IconButton(
                    onClick = { /* Handle more options */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.MoreHoriz,
                        contentDescription = "More options",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        
        // Divider
        HorizontalDivider(
            color = Color.Gray.copy(alpha = 0.2f),
            thickness = 0.5.dp
        )
    }
}

@Composable
private fun EngagementButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int?,
    tint: Color = Color.Gray,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        count?.let {
            if (it > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatCount(it),
                    fontSize = 13.sp,
                    color = tint
                )
            }
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 1_000 -> "${count / 1_000}K"
        else -> count.toString()
    }
}

private fun formatTimeAgo(timestamp: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = sdf.parse(timestamp)
        val now = System.currentTimeMillis()
        val diff = now - (date?.time ?: 0)
        
        when {
            diff < 60_000 -> "now"
            diff < 3_600_000 -> "${diff / 60_000}m"
            diff < 86_400_000 -> "${diff / 3_600_000}h"
            diff < 2_592_000_000 -> "${diff / 86_400_000}d"
            else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        }
    } catch (e: Exception) {
        "now"
    }
}
