package com.example.pulsefeed.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pulsefeed.ui.viewmodel.CreatePostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onNavigateBack: () -> Unit,
    onPostCreated: () -> Unit,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var content by remember { mutableStateOf("") }
    var selectedMedia by remember { mutableStateOf<String?>(null) }
    var characterCount by remember { mutableStateOf(0) }
    
    val maxCharacters = 280
    
    LaunchedEffect(content) {
        characterCount = content.length
    }
    
    LaunchedEffect(uiState.isPostCreated) {
        if (uiState.isPostCreated) {
            viewModel.resetState()
            onPostCreated()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Bar
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            },
            actions = {
                Button(
                    onClick = {
                        if (content.isNotBlank()) {
                            viewModel.createPost(content, selectedMedia)
                        }
                    },
                    enabled = content.isNotBlank() && !uiState.isLoading && characterCount <= maxCharacters,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1DA1F2),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF1DA1F2).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Post",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black
            )
        )
        
        // Main content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile picture
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1DA1F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 24.sp)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    // Text input area
                    BasicTextField(
                        value = content,
                        onValueChange = { 
                            if (it.length <= maxCharacters) {
                                content = it
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 120.dp),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            lineHeight = 24.sp
                        ),
                        decorationBox = { innerTextField ->
                            if (content.isEmpty()) {
                                Text(
                                    "What's happening?",
                                    color = Color.Gray,
                                    fontSize = 18.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    // Media preview
                    selectedMedia?.let { media ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1C1C1E))
                                .border(1.dp, Color(0xFF2F3336), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = media,
                                fontSize = 48.sp
                            )
                            
                            // Remove media button
                            IconButton(
                                onClick = { selectedMedia = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(32.dp)
                                    .background(
                                        Color.Black.copy(alpha = 0.7f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove media",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Error handling
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Red.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Bottom toolbar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Media options
                Row {
                    IconButton(
                        onClick = { selectedMedia = "📷" }
                    ) {
                        Icon(
                            Icons.Outlined.Image,
                            contentDescription = "Add photo",
                            tint = Color(0xFF1DA1F2),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { selectedMedia = "🎥" }
                    ) {
                        Icon(
                            Icons.Outlined.Videocam,
                            contentDescription = "Add video",
                            tint = Color(0xFF1DA1F2),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { selectedMedia = "📊" }
                    ) {
                        Icon(
                            Icons.Outlined.Poll,
                            contentDescription = "Add poll",
                            tint = Color(0xFF1DA1F2),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { selectedMedia = "😊" }
                    ) {
                        Icon(
                            Icons.Outlined.EmojiEmotions,
                            contentDescription = "Add emoji",
                            tint = Color(0xFF1DA1F2),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { selectedMedia = "📍" }
                    ) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = "Add location",
                            tint = Color(0xFF1DA1F2),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Character count
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Character count circle
                    Box(
                        modifier = Modifier.size(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = characterCount.toFloat() / maxCharacters,
                            modifier = Modifier.size(20.dp),
                            color = when {
                                characterCount > maxCharacters -> Color.Red
                                characterCount > maxCharacters * 0.8 -> Color(0xFFFFD700)
                                else -> Color(0xFF1DA1F2)
                            },
                            strokeWidth = 2.dp,
                            trackColor = Color(0xFF2F3336)
                        )
                    }
                    
                    if (characterCount > maxCharacters * 0.8) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${maxCharacters - characterCount}",
                            color = when {
                                characterCount > maxCharacters -> Color.Red
                                characterCount > maxCharacters * 0.9 -> Color(0xFFFFD700)
                                else -> Color.Gray
                            },
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
