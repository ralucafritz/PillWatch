package com.example.pillwatch.di

import com.example.pillwatch.storage.SharedPreferencesStorage
import com.example.pillwatch.storage.Storage
import dagger.Binds
import dagger.Module

@Module
abstract class StorageModule {
    @Binds
    abstract fun provideStorage(storage: SharedPreferencesStorage): Storage
}