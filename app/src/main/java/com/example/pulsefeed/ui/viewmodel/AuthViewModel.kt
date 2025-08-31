package com.example.pulsefeed.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefeed.data.model.AuthResponse
import com.example.pulsefeed.data.model.User
import com.example.pulsefeed.data.repository.UserRepository
import com.example.pulsefeed.ui.viewmodel.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    private val _registerState = MutableStateFlow(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()
    
    fun sendOTP(phoneNumber: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // TODO: Implement actual OTP sending via backend
                // For now, simulate OTP sending
                delay(1500)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
                // In real implementation, this would call backend API
                // repository.sendOTP(phoneNumber)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to send OTP. Please try again."
                )
            }
        }
    }
    
    fun verifyOTP(phoneNumber: String, otp: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // TODO: Implement actual OTP verification via backend
                // For now, simulate OTP verification
                delay(1500)
                
                // Demo: Accept any 6-digit code for testing
                if (otp.length == 6 && otp.all { it.isDigit() }) {
                    val mockAuthResponse = AuthResponse(
                        accessToken = "mock_jwt_token_${System.currentTimeMillis()}",
                        refreshToken = "mock_refresh_token_${System.currentTimeMillis()}",
                        user = User(
                            id = 1,
                            username = phoneNumber.filter { it.isDigit() }.takeLast(10),
                            email = "$phoneNumber@pulsefeed.com",
                            fullName = "User ${phoneNumber.takeLast(4)}",
                            bio = "Welcome to PulseFeed!",
                            avatar = "👤",
                            isVerified = false,
                            createdAt = "2024-01-01T00:00:00Z",
                            updatedAt = "2024-01-01T00:00:00Z",
                            followersCount = 0,
                            followingCount = 0,
                            postsCount = 0,
                            isFollowing = false
                        )
                    )
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        authResponse = mockAuthResponse,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Invalid OTP. Please enter a valid 6-digit code."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to verify OTP. Please try again."
                )
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            _loginState.value = _loginState.value.copy(isLoading = true, error = null)
            
            val result = userRepository.login(username, password)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()!!
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    authResponse = authResponse,
                    isLoggedIn = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    fun register(username: String, email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = userRepository.register(username, email, password, displayName)
            if (result.isSuccess) {
                val authResponse = result.getOrNull()!!
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    authResponse = authResponse,
                    isLoggedIn = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _uiState.value = _uiState.value.copy(
                isLoggedIn = false,
                authResponse = null
            )
        }
    }
    
    suspend fun checkLoginStatus(): Boolean {
        return userRepository.isLoggedIn()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

