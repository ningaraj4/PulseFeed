package com.example.pulsefeed.data.database

import androidx.room.*
import com.example.pulsefeed.data.model.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    
    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun getNotificationsByUserId(userId: Int): List<Notification>
    
    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC")
    fun getNotificationsByUserIdFlow(userId: Int): Flow<List<Notification>>
    
    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    suspend fun getNotificationById(notificationId: Int): Notification?
    
    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = 0")
    suspend fun getUnreadNotificationsCount(userId: Int): Int
    
    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = 0")
    fun getUnreadNotificationsCountFlow(userId: Int): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<Notification>)
    
    @Update
    suspend fun updateNotification(notification: Notification)
    
    @Query("UPDATE notifications SET is_read = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: Int)
    
    @Query("UPDATE notifications SET is_read = 1 WHERE user_id = :userId")
    suspend fun markAllNotificationsAsRead(userId: Int)
    
    @Delete
    suspend fun deleteNotification(notification: Notification)
    
    @Query("DELETE FROM notifications WHERE user_id = :userId")
    suspend fun deleteNotificationsByUserId(userId: Int)
    
    @Query("DELETE FROM notifications")
    suspend fun deleteAllNotifications()
}
