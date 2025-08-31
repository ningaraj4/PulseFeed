package com.example.pulsefeed.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsefeed.data.model.SampleData
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToPost: (Int) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    var isFollowing by remember { mutableStateOf(false) }
    var showStoryHighlights by remember { mutableStateOf(true) }
    var isEditingBio by remember { mutableStateOf(false) }
    var bioText by remember { mutableStateOf("") }
    var showProfileOptions by remember { mutableStateOf(false) }
    var profileViewMode by remember { mutableStateOf("grid") } // grid, list, magazine
    var showAchievements by remember { mutableStateOf(false) }
    var pulseAnimation by remember { mutableStateOf(false) }
    
    // Sample user data - in real app, fetch based on userId
    val user = SampleData.sampleUsers.firstOrNull { it.id == userId } ?: SampleData.sampleUsers.first()
    val userPosts = SampleData.samplePosts.filter { it.userId == userId }.take(12)
    
    // Initialize bio text
    LaunchedEffect(user) {
        bioText = user.bio ?: "✨ Living my best life | 🚀 Building the future | 💫 Dream big, achieve bigger"
    }
    
    // Pulse animation effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            pulseAnimation = true
            delay(500)
            pulseAnimation = false
        }
    }
    
    // Advanced animation states
    val followButtonScale by animateFloatAsState(
        targetValue = if (isFollowing) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "followButtonScale"
    )
    
    val followButtonColor by animateColorAsState(
        targetValue = if (isFollowing) Color(0xFF1DA1F2) else Color.Transparent,
        animationSpec = tween(400),
        label = "followButtonColor"
    )
    
    val profilePulse by animateFloatAsState(
        targetValue = if (pulseAnimation) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "profilePulse"
    )
    
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F0F23),
                        Color.Black
                    ),
                    radius = 1200f
                )
            )
    ) {
        // Floating particles animation background
        repeat(8) { index ->
            val animatedOffset by rememberInfiniteTransition(label = "particles").animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (8000 + index * 1000),
                        easing = LinearEasing
                    )
                ),
                label = "particleRotation$index"
            )
            
            Box(
                modifier = Modifier
                    .offset(
                        x = (50 + index * 45).dp,
                        y = (100 + index * 80).dp
                    )
                    .size((8 + index * 2).dp)
                    .rotate(animatedOffset)
                    .background(
                        Color(0xFF1DA1F2).copy(alpha = 0.1f + index * 0.05f),
                        CircleShape
                    )
            )
        }
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Premium Top Bar with glassmorphism effect
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = user.username,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        if (user.isVerified) {
                            Icon(
                                Icons.Filled.Verified,
                                contentDescription = "Verified",
                                tint = Color(0xFF1DA1F2),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.2f),
                                        Color.White.copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    // Profile options menu
                    IconButton(
                        onClick = { showProfileOptions = !showProfileOptions }
                    ) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "Options",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // View mode toggle
                    IconButton(
                        onClick = { 
                            profileViewMode = when (profileViewMode) {
                                "grid" -> "list"
                                "list" -> "magazine"
                                else -> "grid"
                            }
                        }
                    ) {
                        Icon(
                            when (profileViewMode) {
                                "grid" -> Icons.Filled.GridView
                                "list" -> Icons.Filled.ViewList
                                else -> Icons.Filled.ViewModule
                            },
                            contentDescription = "View Mode",
                            tint = Color(0xFF1DA1F2),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    // Premium Profile Header with glassmorphism
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.05f)
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF667eea),
                                            Color(0xFF764ba2),
                                            Color(0xFF1DA1F2)
                                        )
                                    )
                                )
                        ) {
                            // Animated floating elements in cover
                            repeat(6) { index ->
                                val floatAnimation by rememberInfiniteTransition(label = "float").animateFloat(
                                    initialValue = 0f,
                                    targetValue = 20f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(
                                            durationMillis = 2000 + index * 300,
                                            easing = FastOutSlowInEasing
                                        ),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "floatElement$index"
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .offset(
                                            x = (20 + index * 60).dp,
                                            y = (30 + index * 20).dp + floatAnimation.dp
                                        )
                                        .size((12 + index * 4).dp)
                                        .background(
                                            Color.White.copy(alpha = 0.2f + index * 0.05f),
                                            CircleShape
                                        )
                                )
                            }
                            
                            // Profile picture with advanced styling
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .offset(x = 20.dp, y = 40.dp)
                                    .scale(profilePulse)
                            ) {
                                // Glow effect
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFF1DA1F2).copy(alpha = 0.4f),
                                                    Color.Transparent
                                                ),
                                                radius = 100f
                                            ),
                                            shape = CircleShape
                                        )
                                )
                                
                                // Profile image with gradient border
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .align(Alignment.Center)
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF1DA1F2),
                                                    Color(0xFF667eea),
                                                    Color(0xFF764ba2)
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                        .padding(4.dp)
                                        .background(Color.Black, CircleShape)
                                        .clickable { showAchievements = !showAchievements }
                                ) {
                                    // Profile image placeholder
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(
                                                        Color(0xFF667eea),
                                                        Color(0xFF764ba2)
                                                    )
                                                ),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.username.first().uppercase(),
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                                
                                // Online status indicator
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(24.dp)
                                        .background(Color.Black, CircleShape)
                                        .padding(2.dp)
                                        .background(Color(0xFF00FF00), CircleShape)
                                )
                            }
                        }
                    }
                }
                
                item {
                    // Profile Info Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(60.dp))
                        
                        // Name and verification
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = user.fullName,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (user.isVerified) {
                                Icon(
                                    Icons.Filled.Verified,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF1DA1F2),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            // Premium badge
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFD700).copy(alpha = 0.2f)
                                ),
                                border = BorderStroke(1.dp, Color(0xFFFFD700))
                            ) {
                                Text(
                                    text = "PRO",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD700)
                                )
                            }
                        }
                        
                        Text(
                            text = "@${user.username}",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Interactive Bio Section
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isEditingBio = !isEditingBio },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.05f)
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                        ) {
                            if (isEditingBio) {
                                OutlinedTextField(
                                    value = bioText,
                                    onValueChange = { bioText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    placeholder = { Text("Tell the world about yourself...", color = Color.Gray) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = Color(0xFF1DA1F2),
                                        unfocusedBorderColor = Color.Gray
                                    ),
                                    maxLines = 4
                                )
                            } else {
                                Text(
                                    text = bioText,
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Stats Row with animations
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf(
                                Triple("Posts", userPosts.size.toString(), Icons.Filled.Article),
                                Triple("Followers", "12.5K", Icons.Filled.People),
                                Triple("Following", "1.2K", Icons.Filled.PersonAdd),
                                Triple("Likes", "45.8K", Icons.Filled.Favorite)
                            ).forEach { (label, count, icon) ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                        .clickable { },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.05f)
                                    ),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            icon,
                                            contentDescription = label,
                                            tint = Color(0xFF1DA1F2),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = count,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Follow Button with advanced animation
                            Button(
                                onClick = { isFollowing = !isFollowing },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .scale(followButtonScale),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = followButtonColor
                                ),
                                border = BorderStroke(
                                    2.dp,
                                    if (isFollowing) Color(0xFF1DA1F2) else Color.White
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        if (isFollowing) Icons.Filled.Check else Icons.Filled.PersonAdd,
                                        contentDescription = null,
                                        tint = if (isFollowing) Color.White else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (isFollowing) "Following" else "Follow",
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isFollowing) Color.White else Color.White
                                    )
                                }
                            }
                            
                            // Message Button
                            Button(
                                onClick = { },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ),
                                border = BorderStroke(2.dp, Color(0xFF1DA1F2))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Message,
                                        contentDescription = null,
                                        tint = Color(0xFF1DA1F2),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Message",
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1DA1F2)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                
                // Story Highlights Section
                if (showStoryHighlights) {
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Story Highlights",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                TextButton(
                                    onClick = { showStoryHighlights = false }
                                ) {
                                    Text("Hide", color = Color(0xFF1DA1F2))
                                }
                            }
                            
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                items(5) { index ->
                                    Card(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clickable { },
                                        shape = CircleShape,
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White.copy(alpha = 0.05f)
                                        ),
                                        border = BorderStroke(2.dp, Color(0xFF1DA1F2))
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                listOf(
                                                    Icons.Filled.Camera,
                                                    Icons.Filled.Favorite,
                                                    Icons.Filled.Star,
                                                    Icons.Filled.Flight,
                                                    Icons.Filled.Work
                                                )[index],
                                                contentDescription = null,
                                                tint = Color(0xFF1DA1F2),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
                
                // Enhanced Tab Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.05f)
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Column {
                            // Custom Tab Row with animations
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                val tabs = listOf(
                                    Triple("Posts", Icons.Filled.Article, userPosts.size),
                                    Triple("Media", Icons.Filled.Photo, 24),
                                    Triple("Replies", Icons.Filled.Reply, 156),
                                    Triple("Likes", Icons.Filled.Favorite, 892)
                                )
                                
                                tabs.forEachIndexed { index, (title, icon, count) ->
                                    val isSelected = selectedTab == index
                                    val tabColor by animateColorAsState(
                                        targetValue = if (isSelected) Color(0xFF1DA1F2) else Color.Gray,
                                        animationSpec = tween(300),
                                        label = "tabColor$index"
                                    )
                                    
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 2.dp)
                                            .clickable { selectedTab = index },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) 
                                                Color(0xFF1DA1F2).copy(alpha = 0.2f) 
                                            else Color.Transparent
                                        ),
                                        border = if (isSelected) 
                                            BorderStroke(1.dp, Color(0xFF1DA1F2)) 
                                        else null
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                icon,
                                                contentDescription = title,
                                                tint = tabColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = title,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = tabColor
                                            )
                                            if (isSelected) {
                                                Text(
                                                    text = count.toString(),
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF1DA1F2),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Content based on selected tab and view mode
                when (selectedTab) {
                    0 -> { // Posts
                        when (profileViewMode) {
                            "grid" -> {
                                items(userPosts.chunked(2)) { rowPosts ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowPosts.forEach { post ->
                                            EnhancedProfilePostCard(
                                                post = post,
                                                user = user,
                                                onPostClick = { onNavigateToPost(post.id) }
                                            )
                                        }
                                        if (rowPosts.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                            "list" -> {
                                items(userPosts) { post ->
                                    EnhancedProfilePostCard(
                                        post = post,
                                        user = user,
                                        onPostClick = { onNavigateToPost(post.id) }
                                    )
                                }
                            }
                            "magazine" -> {
                                items(userPosts) { post ->
                                    EnhancedProfilePostCard(
                                        post = post,
                                        user = user,
                                        onPostClick = { onNavigateToPost(post.id) }
                                    )
                                }
                            }
                        }
                    }
                    1 -> { // Media
                        item {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(9) { index ->
                                    Card(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .clickable { },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White.copy(alpha = 0.05f)
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Filled.Photo,
                                                contentDescription = null,
                                                tint = Color(0xFF1DA1F2),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2, 3 -> { // Replies & Likes
                        item {
                            PremiumEmptyState(
                                title = if (selectedTab == 2) "No Replies Yet" else "No Liked Posts",
                                description = if (selectedTab == 2) 
                                    "When ${user.username} replies to posts, they'll appear here" 
                                else "Posts that ${user.username} has liked will show up here",
                                icon = if (selectedTab == 2) Icons.Filled.Reply else Icons.Filled.Favorite
                            )
                        }
                    }
                }
            }
        }
        
        // Achievements Overlay
        AnimatedVisibility(
            visible = showAchievements,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            AchievementsOverlay(
                onDismiss = { showAchievements = false }
            )
        }
        
        // Profile Options Menu
        AnimatedVisibility(
            visible = showProfileOptions,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            ProfileOptionsMenu(
                onDismiss = { showProfileOptions = false }
            )
        }
    }
}

// Enhanced Post Card Components
@Composable
private fun EnhancedPostCard(
    post: com.example.pulsefeed.data.model.SamplePost,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1DA1F2).copy(alpha = 0.1f),
                                Color(0xFF667eea).copy(alpha = 0.1f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = post.content,
                    color = Color.White,
                    fontSize = 14.sp,                                                                                   
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = post.likes.toString(),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    
                    Text(
                        text = post.timestamp,
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedListPostCard(
    post: com.example.pulsefeed.data.model.SamplePost,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile picture
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1DA1F2),
                                Color(0xFF667eea)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "U",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = post.content,
                    color = Color.White,
                    fontSize = 16.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = post.likes.toString(),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Filled.Comment,
                            contentDescription = null,
                            tint = Color(0xFF1DA1F2),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = post.comments.toString(),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MagazinePostCard(
    post: com.example.pulsefeed.data.model.SamplePost,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667eea),
                                Color(0xFF764ba2),
                                Color(0xFF1DA1F2)
                            )
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .background(
                            Color.Black.copy(alpha = 0.7f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Featured",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = post.content,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFE91E63),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = post.likes.toString(),
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.Comment,
                                contentDescription = null,
                                tint = Color(0xFF1DA1F2),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = post.comments.toString(),
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    Text(
                        text = post.timestamp,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumEmptyState(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1DA1F2).copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFF1DA1F2),
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun AchievementsOverlay(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clickable { },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🏆 Achievements",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(5) { index ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFD700).copy(alpha = 0.2f)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFFFD700))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = listOf("🥇", "⭐", "🚀", "💎", "🔥")[index],
                                    fontSize = 32.sp
                                )
                                Text(
                                    text = listOf("First Post", "100 Likes", "Verified", "Premium", "Trending")[index],
                                    fontSize = 12.sp,
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1DA1F2)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ProfileOptionsMenu(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.TopEnd
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .clickable { },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                listOf(
                    Triple("Share Profile", Icons.Filled.Share, {}),
                    Triple("Copy Link", Icons.Filled.Link, {}),
                    Triple("Block User", Icons.Filled.Block, {}),
                    Triple("Report", Icons.Filled.Report, {})
                ).forEach { (text, icon, action) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { action() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = text,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedProfilePostCard(
    post: com.example.pulsefeed.data.model.SamplePost,
    user: com.example.pulsefeed.data.model.SampleUser,
    onPostClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPostClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF16181C)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.timestamp,
                    color = Color(0xFF71767B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                IconButton(
                    onClick = { /* More options */ },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(
                        Icons.Outlined.MoreHoriz,
                        contentDescription = "More",
                        tint = Color(0xFF71767B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = post.content,
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
            
            post.imageUrl?.let { _ ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F1419)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📷", fontSize = 36.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { /* Comment action */ }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Comment,
                        contentDescription = "Comments",
                        tint = Color(0xFF71767B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatNumber(post.comments),
                        color = Color(0xFF71767B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { /* Repost action */ }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Repeat,
                        contentDescription = "Repost",
                        tint = Color(0xFF71767B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatNumber(post.likes / 10),
                        color = Color(0xFF71767B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { /* Like action */ }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        if (post.isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) Color(0xFFE91E63) else Color(0xFF71767B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatNumber(post.likes),
                        color = if (post.isLiked) Color(0xFFE91E63) else Color(0xFF71767B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                IconButton(
                    onClick = { /* Share action */ },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF71767B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedEmptyTabContent(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF16181C)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color(0xFF71767B),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = subtitle,
                color = Color(0xFF71767B),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

private fun formatNumber(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 1_000 -> "${count / 1_000}K"
        else -> count.toString()
    }
}
