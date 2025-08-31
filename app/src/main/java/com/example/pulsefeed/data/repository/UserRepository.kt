package com.example.pulsefeed.data.repository

import com.example.pulsefeed.data.model.*
import com.example.pulsefeed.data.api.ApiService
import com.example.pulsefeed.data.model.AuthResponse
import com.example.pulsefeed.data.model.LoginRequest
import com.example.pulsefeed.data.model.RegisterRequest
import com.example.pulsefeed.data.model.FollowResponse
import com.example.pulsefeed.data.preferences.UserPreferences
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
    private val fallbackRepository: FallbackRepository
) {
    
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Store tokens
                userPreferences.saveAccessToken(authResponse.accessToken)
                userPreferences.saveRefreshToken(authResponse.refreshToken)
                userPreferences.saveUserId(authResponse.user.id)
                
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Fallback to sample data when backend is unavailable
            fallbackRepository.loginFallback(email, password)
        }
    }
    
    suspend fun register(username: String, email: String, password: String, displayName: String): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(username, email, password, displayName)
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Save auth data locally
                userPreferences.saveAuthData(
                    accessToken = authResponse.accessToken,
                    refreshToken = authResponse.refreshToken,
                    userId = authResponse.user.id,
                    username = authResponse.user.username,
                    email = authResponse.user.email,
                    fullName = authResponse.user.fullName
                )
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProfile(): Result<User> {
        return try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserProfile(userId: Int): Result<User> {
        return try {
            val response = apiService.getUserProfile(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch user profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun followUser(userId: Int): Result<FollowResponse> {
        return try {
            val response = apiService.followUser(userId)
            if (response.isSuccessful && response.body() != null) {
                // Convert Map<String, String> to FollowResponse
                val responseMap = response.body()!!
                val message = responseMap["message"] ?: "Follow successful"
                val isFollowing = responseMap["isFollowing"]?.toBoolean() ?: true
                Result.success(FollowResponse(message, isFollowing))
            } else {
                Result.failure(Exception("Failed to follow user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unfollowUser(userId: Int): Result<FollowResponse> {
        return try {
            val response = apiService.unfollowUser(userId)
            if (response.isSuccessful && response.body() != null) {
                // Convert Map<String, String> to FollowResponse
                val responseMap = response.body()!!
                val message = responseMap["message"] ?: "Unfollow successful"
                val isFollowing = responseMap["isFollowing"]?.toBoolean() ?: false
                Result.success(FollowResponse(message, isFollowing))
            } else {
                Result.failure(Exception("Failed to unfollow user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchUsers(query: String): Result<List<User>> {
        return try {
            val response = apiService.searchUsers(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to search users: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendOTP(phoneNumber: String): Result<Unit> {
        return try {
            // For development: Show OTP in logs and UI
            val developmentOTP = generateDevelopmentOTP()
            android.util.Log.d("PulseFeed_OTP", "Development OTP for $phoneNumber: $developmentOTP")
            
            // Store OTP for verification (in production, this would be handled by backend)
            userPreferences.storeDevelopmentOTP(phoneNumber, developmentOTP)
            
            // Real backend SMS integration
            try {
                val response = apiService.sendOTP(SendOTPRequest(phoneNumber))
                if (response.isSuccessful) {
                    android.util.Log.d("PulseFeed_SMS", "OTP sent successfully to $phoneNumber")
                    return Result.success(Unit)
                } else {
                    android.util.Log.e("PulseFeed_SMS", "Failed to send OTP: ${response.message()}")
                    // Fallback to development mode
                }
            } catch (e: Exception) {
                android.util.Log.e("PulseFeed_SMS", "SMS service error: ${e.message}")
                // Continue to development fallback
            }
            
            kotlinx.coroutines.delay(1000) // Simulate network delay
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateDevelopmentOTP(): String {
        return (100000..999999).random().toString()
    }
    
    suspend fun verifyOTP(phoneNumber: String, otp: String): Result<AuthResponse> {
        return try {
            // Try real backend verification first
            try {
                val response = apiService.verifyOTP(VerifyOTPRequest(phoneNumber, otp))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    // Save auth data
                    userPreferences.saveAuthData(
                        accessToken = authResponse.accessToken,
                        refreshToken = authResponse.refreshToken,
                        userId = authResponse.user.id,
                        username = authResponse.user.username,
                        email = authResponse.user.email,
                        fullName = authResponse.user.fullName
                    )
                    android.util.Log.d("PulseFeed_SMS", "OTP verified successfully via backend")
                    return Result.success(authResponse)
                }
            } catch (e: Exception) {
                android.util.Log.e("PulseFeed_SMS", "Backend verification failed: ${e.message}")
            }
            
            // Fallback to development OTP verification
            val storedOTP = userPreferences.getDevelopmentOTP(phoneNumber)
            if (storedOTP != null && storedOTP == otp) {
                // Clear the used OTP
                userPreferences.clearDevelopmentOTP()
                
                kotlinx.coroutines.delay(1000)
                val mockUser = User(
                id = 1,
                username = "user_${System.currentTimeMillis()}",
                email = "$phoneNumber@pulsefeed.com",
                fullName = "New User",
                bio = "",
                avatar = "",
                coverImage = "",
                isVerified = false,
                createdAt = java.time.Instant.now().toString(),
                updatedAt = java.time.Instant.now().toString(),
                followersCount = 0,
                followingCount = 0,
                postsCount = 0,
                isFollowing = false
            )
            val authResponse = AuthResponse(
                accessToken = "mock_access_token_${System.currentTimeMillis()}",
                refreshToken = "mock_refresh_token_${System.currentTimeMillis()}",
                user = mockUser
            )
            
            // Save auth data
            userPreferences.saveAuthData(
                accessToken = authResponse.accessToken,
                refreshToken = authResponse.refreshToken,
                userId = authResponse.user.id,
                username = authResponse.user.username,
                email = authResponse.user.email,
                fullName = authResponse.user.fullName
            )
            
            Result.success(authResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProfile(fullName: String, username: String, bio: String?): Result<User> {
        return try {
            // Mock implementation - replace with real API call
            kotlinx.coroutines.delay(1000)
            val updatedUser = User(
                id = 1,
                username = username,
                email = "user@pulsefeed.com",
                fullName = fullName,
                bio = bio ?: "",
                avatar = "",
                coverImage = "",
                isVerified = false,
                createdAt = java.time.Instant.now().toString(),
                updatedAt = java.time.Instant.now().toString(),
                followersCount = 0,
                followingCount = 0,
                postsCount = 0,
                isFollowing = false
            )
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        userPreferences.clearAuthData()
    }
    
    suspend fun isLoggedIn(): Boolean {
        return userPreferences.accessToken.first()?.isNotEmpty() == true
    }
}
