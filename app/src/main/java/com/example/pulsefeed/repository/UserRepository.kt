package com.example.pulsefeed.repository

import com.example.pulsefeed.data.api.ApiService
import com.example.pulsefeed.data.database.UserDao
import com.example.pulsefeed.data.model.UpdateProfileRequest
import com.example.pulsefeed.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) {
    
    suspend fun getProfile(): Result<User> {
        return try {
            val response = apiService.getProfile()
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                userDao.insertUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to get profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProfile(
        fullName: String? = null,
        bio: String? = null,
        avatar: String? = null
    ): Result<User> {
        return try {
            val request = UpdateProfileRequest(fullName, bio, avatar)
            val response = apiService.updateProfile(request)
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                userDao.updateUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to update profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserProfile(userId: Int): Result<User> {
        return try {
            val response = apiService.getUserProfile(userId)
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                userDao.insertUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to get user profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            // Try to get from local database
            val localUser = userDao.getUserById(userId)
            if (localUser != null) {
                Result.success(localUser)
            } else {
                Result.failure(e)
            }
        }
    }
    
    suspend fun followUser(userId: Int): Result<String> {
        return try {
            val response = apiService.followUser(userId)
            if (response.isSuccessful) {
                Result.success("User followed successfully")
            } else {
                Result.failure(Exception("Failed to follow user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unfollowUser(userId: Int): Result<String> {
        return try {
            val response = apiService.unfollowUser(userId)
            if (response.isSuccessful) {
                Result.success("User unfollowed successfully")
            } else {
                Result.failure(Exception("Failed to unfollow user: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFollowers(userId: Int): Result<List<User>> {
        return try {
            val response = apiService.getFollowers(userId)
            if (response.isSuccessful && response.body() != null) {
                val followers = response.body()!!
                userDao.insertUsers(followers)
                Result.success(followers)
            } else {
                Result.failure(Exception("Failed to get followers: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFollowing(userId: Int): Result<List<User>> {
        return try {
            val response = apiService.getFollowing(userId)
            if (response.isSuccessful && response.body() != null) {
                val following = response.body()!!
                userDao.insertUsers(following)
                Result.success(following)
            } else {
                Result.failure(Exception("Failed to get following: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchUsers(query: String): Result<List<User>> {
        return try {
            val response = apiService.searchUsers(query)
            if (response.isSuccessful && response.body() != null) {
                val users = response.body()!!
                userDao.insertUsers(users)
                Result.success(users)
            } else {
                // Try local search as fallback
                val localUsers = userDao.searchUsers(query)
                Result.success(localUsers)
            }
        } catch (e: Exception) {
            // Try local search as fallback
            val localUsers = userDao.searchUsers(query)
            Result.success(localUsers)
        }
    }
    
    fun getUserByIdFlow(userId: Int): Flow<User?> {
        return userDao.getUserByIdFlow(userId)
    }
}
