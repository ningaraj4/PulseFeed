package com.example.pulsefeed.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.pulsefeed.data.model.*

@Database(
    entities = [User::class, Post::class, Comment::class, Notification::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PulseFeedDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun notificationDao(): NotificationDao
    
    companion object {
        @Volatile
        private var INSTANCE: PulseFeedDatabase? = null
        
        fun getDatabase(context: Context): PulseFeedDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PulseFeedDatabase::class.java,
                    "pulsefeed_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
