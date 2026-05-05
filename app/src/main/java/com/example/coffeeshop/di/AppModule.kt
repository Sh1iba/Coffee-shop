package com.example.coffeeshop.di

import android.content.Context
import com.example.coffeeshop.data.managers.ErrorParser
import com.example.coffeeshop.data.managers.LocationManager
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.managers.SearchHistoryManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePrefsManager(@ApplicationContext context: Context): PrefsManager =
        PrefsManager(context)

    @Provides
    @Singleton
    fun provideErrorParser(): ErrorParser = ErrorParser()

    @Provides
    @Singleton
    fun provideSearchHistoryManager(@ApplicationContext context: Context): SearchHistoryManager =
        SearchHistoryManager(context)

    @Provides
    @Singleton
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager =
        LocationManager(context)
}
