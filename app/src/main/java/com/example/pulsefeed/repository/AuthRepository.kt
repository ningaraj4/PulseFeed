package com.example.pulsefeed.repository

import com.example.pulsefeed.data.api.ApiService
import com.example.pulsefeed.data.model.*
import com.example.pulsefeed.data.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    
    val accessToken: Flow<String?> = userPreferences.accessToken
    val isRecentlyLoggedIn: Flow<Boolean> = userPreferences.isRecentlyLoggedIn
    val userId: Flow<Int?> = userPreferences.userId
    val username: Flow<String?> = userPreferences.username
    
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                saveAuthData(authResponse)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(
        username: String,
        email: String,
        password: String,
        fullName: String
    ): Result<AuthResponse> {
        return try {
            val response = apiService.register(
                RegisterRequest(username, email, password, fullName)
            )
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                saveAuthData(authResponse)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun refreshToken(): Result<AuthResponse> {
        return try {
            val response = apiService.refreshToken()
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                saveAuthData(authResponse)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Token refresh failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun googleAuth(token: String): Result<AuthResponse> {
        return try {
            val response = apiService.googleAuth(mapOf("token" to token))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                saveAuthData(authResponse)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Google auth failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        userPreferences.clearAuthData()
    }
    
    private suspend fun saveAuthData(authResponse: AuthResponse) {
        userPreferences.saveAuthData(
            accessToken = authResponse.accessToken,
            refreshToken = authResponse.refreshToken,
            userId = authResponse.user.id,
            username = authResponse.user.username,
            email = authResponse.user.email,
            fullName = authResponse.user.fullName
        )
    }
}
