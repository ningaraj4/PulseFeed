package com.example.pulsefeed.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsefeed.data.model.SampleData

data class TrendingTopic(
    val title: String,
    val category: String,
    val posts: String,
    val description: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToProfile: (Int) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("For You") }
    
    val trendingTopics = listOf(
        TrendingTopic("Gujarat", "Trending in Politics", "15.1K posts"),
        TrendingTopic("Nasser Hospital", "Trending in News", "9,062 posts"),
        TrendingTopic("#Bulandshahr", "Trending in India", "5,234 posts"),
        TrendingTopic("#GaneshChaturthi2025", "Trending in India", "12.4K posts"),
        TrendingTopic("Expats Rally Locals", "News", "33K posts", "for Gurugram's Trash Cleanup Showdown"),
        TrendingTopic("Shehnaaz Gill", "Entertainment", "94K posts", "Unveils Emotional Teaser for Debut Production 'Ikk Kudi'"),
        TrendingTopic("Mumbai Unveils", "News", "3.7K posts", "Majestic Lalbaugcha Raja for Ganesh Chaturthi 2025"),
        TrendingTopic("#TechNews", "Technology", "8.9K posts"),
        TrendingTopic("Cricket World Cup", "Sports", "45.2K posts"),
        TrendingTopic("#AI", "Technology", "23.1K posts")
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    "Search",
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
        
        // Search bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF16181C)
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search PulseFeed",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF1DA1F2).copy(alpha = 0.5f),
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Tab Row
        TabRow(
            selectedTabIndex = when (selectedTab) {
                "For You" -> 0
                "Trending" -> 1
                "News" -> 2
                "Sports" -> 3
                else -> 4
            },
            containerColor = Color.Black,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.offset(x = tabPositions[when (selectedTab) {
                        "For You" -> 0
                        "Trending" -> 1
                        "News" -> 2
                        "Sports" -> 3
                        else -> 4
                    }].left),
                    color = Color(0xFF1DA1F2),
                    height = 3.dp
                )
            }
        ) {
            listOf("For You", "Trending", "News", "Sports", "Entertainment").forEach { tab ->
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
        
        // Content
        if (searchQuery.isBlank()) {
            // Show trending topics
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Text(
                        text = "Today's News",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                items(trendingTopics.filter { 
                    when (selectedTab) {
                        "News" -> it.category.contains("News")
                        "Sports" -> it.category.contains("Sports")
                        "Entertainment" -> it.category.contains("Entertainment")
                        "Trending" -> it.category.contains("Trending")
                        else -> true
                    }
                }) { topic ->
                    TrendingTopicCard(
                        topic = topic,
                        onClick = { /* Navigate to topic */ }
                    )
                    Divider(
                        color = Color(0xFF2F3336),
                        thickness = 0.5.dp
                    )
                }
            }
        } else {
            // Show search results
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(SampleData.sampleUsers.filter { 
                    it.username.contains(searchQuery, ignoreCase = true) ||
                    it.fullName.contains(searchQuery, ignoreCase = true)
                }) { user ->
                    UserSearchCard(
                        user = user,
                        onClick = { onNavigateToProfile(user.id) }
                    )
                    Divider(
                        color = Color(0xFF2F3336),
                        thickness = 0.5.dp
                    )
                }
                
                if (SampleData.sampleUsers.none { 
                    it.username.contains(searchQuery, ignoreCase = true) ||
                    it.fullName.contains(searchQuery, ignoreCase = true)
                }) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Outlined.Search,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "No results for \"$searchQuery\"",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Try searching for something else.",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrendingTopicCard(
    topic: TrendingTopic,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = topic.category,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = topic.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                topic.description?.let { desc ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = desc,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = topic.posts,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
            
            IconButton(onClick = { /* More options */ }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun UserSearchCard(
    user: com.example.pulsefeed.data.model.SampleUser,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile picture
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF1DA1F2)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.avatar,
                fontSize = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.fullName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
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
            }
            Text(
                text = "@${user.username}",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = user.bio,
                color = Color.Gray,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        // Follow button
        Button(
            onClick = { /* Handle follow */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                "Follow",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
