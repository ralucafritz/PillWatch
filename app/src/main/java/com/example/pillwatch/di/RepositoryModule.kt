package com.example.pillwatch.di

import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.MedsLogRepository
import com.example.pillwatch.data.repository.MedsRepository
import com.example.pillwatch.data.repository.MetadataRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.data.source.local.AlarmDao
import com.example.pillwatch.data.source.local.MedsDao
import com.example.pillwatch.data.source.local.MedsLogDao
import com.example.pillwatch.data.source.local.MetadataDao
import com.example.pillwatch.data.source.local.UserDao
import com.example.pillwatch.data.source.local.UserMedsDao
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
    @Provides
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    fun provideMedsRepository(medsDao: MedsDao): MedsRepository {
        return MedsRepository(medsDao)
    }

    @Provides
    fun provideMedsLogRepository(medsLogDao: MedsLogDao): MedsLogRepository {
        return MedsLogRepository(medsLogDao)
    }

    @Provides
    fun provideMetadataRepository(metadataDao: MetadataDao): MetadataRepository {
        return MetadataRepository(metadataDao)
    }

    @Provides
    fun provideAlarmRepository(alarmDao: AlarmDao): AlarmRepository {
        return AlarmRepository(alarmDao)
    }

    @Provides
    fun provideUserMedsRepository(userMedsDao: UserMedsDao): UserMedsRepository {
        return UserMedsRepository(userMedsDao)
    }
}