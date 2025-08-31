package com.example.pulsefeed.di

import android.content.Context
import com.example.pulsefeed.data.database.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PulseFeedDatabase {
        return PulseFeedDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideUserDao(database: PulseFeedDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    fun providePostDao(database: PulseFeedDatabase): PostDao {
        return database.postDao()
    }
    
    @Provides
    fun provideCommentDao(database: PulseFeedDatabase): CommentDao {
        return database.commentDao()
    }
    
    @Provides
    fun provideNotificationDao(database: PulseFeedDatabase): NotificationDao {
        return database.notificationDao()
    }
}
