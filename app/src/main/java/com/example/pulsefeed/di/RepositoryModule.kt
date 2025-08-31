package com.example.pulsefeed.di

import com.example.pulsefeed.data.repository.FallbackRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFallbackRepository(): FallbackRepository {
        return FallbackRepository()
    }
}
