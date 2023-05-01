package com.example.pillwatch.di

import android.content.Context
import androidx.room.Room
import com.example.pillwatch.data.source.local.AlarmDao
import com.example.pillwatch.data.source.local.AppDatabase
import com.example.pillwatch.data.source.local.MedsDao
import com.example.pillwatch.data.source.local.MedsLogDao
import com.example.pillwatch.data.source.local.MetadataDao
import com.example.pillwatch.data.source.local.UserDao
import com.example.pillwatch.data.source.local.UserMedsDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context) : AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "PillWatchDatabase").build()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao  = database.userDao

    @Singleton
    @Provides
    fun provideMedsDao(database: AppDatabase): MedsDao  = database.medsDao

    @Singleton
    @Provides
    fun provideMedsLogDao(database: AppDatabase): MedsLogDao  = database.medsLogDao

    @Singleton
    @Provides
    fun provideMetadataDao(database: AppDatabase): MetadataDao  = database.metadataDao

    @Singleton
    @Provides
    fun provideAlarmDao(database: AppDatabase): AlarmDao  = database.alarmDao

    @Singleton
    @Provides
    fun provideUserMedsDao(database: AppDatabase): UserMedsDao  = database.userMedsDao
}