package com.example.pulsefeed.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {
    
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val FULL_NAME_KEY = stringPreferencesKey("full_name")
        private val LAST_LOGIN_TIME_KEY = stringPreferencesKey("last_login_time")
        private val DEV_OTP_KEY = stringPreferencesKey("dev_otp")
        private val DEV_PHONE_KEY = stringPreferencesKey("dev_phone")
    }
    
    val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }
    
    val refreshToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }
    
    val userId: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }
    
    val username: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USERNAME_KEY]
    }
    
    val email: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[EMAIL_KEY]
    }
    
    val fullName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[FULL_NAME_KEY]
    }
    
    val lastLoginTime: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LAST_LOGIN_TIME_KEY]
    }
    
    // Check if user logged in within last 15 days
    val isRecentlyLoggedIn: Flow<Boolean> = lastLoginTime.map { lastLogin ->
        if (lastLogin.isNullOrEmpty()) return@map false
        
        try {
            val lastLoginMillis = lastLogin.toLong()
            val currentTime = System.currentTimeMillis()
            val fifteenDaysInMillis = 15 * 24 * 60 * 60 * 1000L // 15 days in milliseconds
            
            (currentTime - lastLoginMillis) <= fifteenDaysInMillis
        } catch (e: NumberFormatException) {
            false
        }
    }
    
    suspend fun saveAuthData(
        accessToken: String,
        refreshToken: String,
        userId: Int,
        username: String,
        email: String,
        fullName: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[USER_ID_KEY] = userId
            preferences[USERNAME_KEY] = username
            preferences[EMAIL_KEY] = email
            preferences[FULL_NAME_KEY] = fullName
            preferences[LAST_LOGIN_TIME_KEY] = System.currentTimeMillis().toString()
        }
    }
    
    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(USERNAME_KEY)
            preferences.remove(EMAIL_KEY)
            preferences.remove(FULL_NAME_KEY)
            preferences.remove(LAST_LOGIN_TIME_KEY)
        }
    }
    
    suspend fun updateAccessToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }
    
    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }
    
    suspend fun saveRefreshToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = token
        }
    }
    
    suspend fun saveUserId(id: Int) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = id
        }
    }
    
    // Development OTP methods
    suspend fun storeDevelopmentOTP(phoneNumber: String, otp: String) {
        context.dataStore.edit { preferences ->
            preferences[DEV_PHONE_KEY] = phoneNumber
            preferences[DEV_OTP_KEY] = otp
        }
    }
    
    suspend fun getDevelopmentOTP(phoneNumber: String): String? {
        val preferences = context.dataStore.data.map { prefs ->
            val storedPhone = prefs[DEV_PHONE_KEY]
            val storedOTP = prefs[DEV_OTP_KEY]
            if (storedPhone == phoneNumber) storedOTP else null
        }
        return preferences.map { it }.first()
    }
    
    suspend fun clearDevelopmentOTP() {
        context.dataStore.edit { preferences ->
            preferences.remove(DEV_PHONE_KEY)
            preferences.remove(DEV_OTP_KEY)
        }
    }
}
