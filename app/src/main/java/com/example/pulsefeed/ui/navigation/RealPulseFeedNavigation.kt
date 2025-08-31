package com.example.pulsefeed.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pulsefeed.ui.screen.*
import com.example.pulsefeed.ui.viewmodel.RealAuthViewModel

@Composable
fun RealPulseFeedNavigation() {
    val navController = rememberNavController()
    val authViewModel: RealAuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    
    NavHost(
        navController = navController,
        startDestination = if (authState.isAuthenticated) {
            if (authState.onboardingComplete) "main" else "onboarding"
        } else {
            "auth"
        }
    ) {
        // Authentication Flow
        composable("auth") {
            RealAuthScreen(
                onAuthSuccess = {
                    navController.navigate("onboarding") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        
        // Onboarding Flow
        composable("onboarding") {
            OnboardingScreen(
                phoneNumber = "", // Get from auth state
                onComplete = {
                    navController.navigate("main") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        
        // Main App Flow
        composable("main") {
            MainScreen(
                onNavigateToProfile = { userId ->
                    navController.navigate("profile/$userId")
                },
                onNavigateToPost = { postId ->
                    navController.navigate("post_detail/$postId")
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("auth") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
        
        // Create Post
        composable("create_post") {
            RealCreatePostScreen(
                onNavigateBack = { navController.popBackStack() },
                onPostCreated = { navController.popBackStack() }
            )
        }
        
        // Contact Invite
        composable("invite_contacts") {
            ContactInviteScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Profile Detail
        composable("profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 1
            ModernProfileScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPost = { postId ->
                    navController.navigate("post_detail/$postId")
                }
            )
        }
        
        // Post Detail
        composable("post_detail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")?.toIntOrNull() ?: 1
            PostDetailScreen(
                postId = postId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToProfile = { userId ->
                    navController.navigate("profile/$userId")
                }
            )
        }
    }
}
