package com.example.pulsefeed.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pulsefeed.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    phoneNumber: String,
    onComplete: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var currentStep by remember { mutableStateOf(0) }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F23),
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // Progress Indicator
            LinearProgressIndicator(
                progress = (currentStep + 1) / 3f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color(0xFF1DA1F2),
                trackColor = Color.Gray.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            when (currentStep) {
                0 -> {
                    // Step 1: Full Name
                    Text(
                        text = "What's your name?",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "This will be displayed on your profile",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1DA1F2),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF1DA1F2),
                            unfocusedLabelColor = Color.Gray
                        ),
                        placeholder = {
                            Text("Enter your full name", color = Color.Gray.copy(alpha = 0.7f))
                        }
                    )
                }
                
                1 -> {
                    // Step 2: Username
                    Text(
                        text = "Choose a username",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "This is how others will find you",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    OutlinedTextField(
                        value = username,
                        onValueChange = { 
                            // Only allow alphanumeric and underscores
                            val filtered = it.filter { char -> 
                                char.isLetterOrDigit() || char == '_' 
                            }.lowercase()
                            if (filtered.length <= 20) {
                                username = filtered
                            }
                        },
                        label = { Text("Username", color = Color.Gray) },
                        leadingIcon = {
                            Text("@", color = Color(0xFF1DA1F2), fontSize = 18.sp)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1DA1F2),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF1DA1F2),
                            unfocusedLabelColor = Color.Gray
                        ),
                        placeholder = {
                            Text("username", color = Color.Gray.copy(alpha = 0.7f))
                        }
                    )
                    
                    Text(
                        text = "${username.length}/20 characters",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 16.dp)
                    )
                }
                
                2 -> {
                    // Step 3: Bio (Optional)
                    Text(
                        text = "Tell us about yourself",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Write a short bio (optional)",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { 
                            if (it.length <= 160) {
                                bio = it
                            }
                        },
                        label = { Text("Bio", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 4,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1DA1F2),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF1DA1F2),
                            unfocusedLabelColor = Color.Gray
                        ),
                        placeholder = {
                            Text("Tell people a bit about yourself...", color = Color.Gray.copy(alpha = 0.7f))
                        }
                    )
                    
                    Text(
                        text = "${bio.length}/160 characters",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (currentStep == 0) Arrangement.End else Arrangement.SpaceBetween
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Back", fontSize = 16.sp)
                    }
                }
                
                Button(
                    onClick = {
                        if (currentStep < 2) {
                            currentStep++
                        } else {
                            // Complete onboarding
                            // TODO: Implement onboarding completion
                        }
                        onComplete()
                    },
                    enabled = when (currentStep) {
                        0 -> fullName.isNotBlank()
                        1 -> username.isNotBlank() && username.length >= 3
                        2 -> true // Bio is optional
                        else -> false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1DA1F2),
                        disabledContainerColor = Color(0xFF1DA1F2).copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        if (currentStep < 2) "Next" else "Complete",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            if (currentStep == 2) {
                Spacer(modifier = Modifier.height(24.dp))
                
                TextButton(
                    onClick = {
                        // TODO: Implement onboarding completion
                        onComplete()
                    }
                ) {
                    Text(
                        "Skip for now",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
