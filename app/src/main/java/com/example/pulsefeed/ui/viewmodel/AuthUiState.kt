package com.example.pulsefeed.ui.viewmodel

import com.example.pulsefeed.data.model.AuthResponse

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val authResponse: AuthResponse? = null,
    val isLoggedIn: Boolean = false
)

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null
)

data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null
)
