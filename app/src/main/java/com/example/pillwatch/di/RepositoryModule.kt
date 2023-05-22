package com.example.pillwatch.di

import com.example.pillwatch.data.repository.AlarmFirestoreRepository
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.MedsLogFirestoreRepository
import com.example.pillwatch.data.repository.MedsLogRepository
import com.example.pillwatch.data.repository.MedsRepository
import com.example.pillwatch.data.repository.MetadataRepository
import com.example.pillwatch.data.repository.UserFirestoreRepository
import com.example.pillwatch.data.repository.UserMedsFirestoreRepository
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
    fun provideUserRepository(
        userDao: UserDao,
        userFirestoreRepository: UserFirestoreRepository
    ): UserRepository {
        return UserRepository(userDao, userFirestoreRepository)
    }

    @Provides
    fun provideUserFirestoreRep(): UserFirestoreRepository{
        return UserFirestoreRepository()
    }

    @Provides
    fun provideMedsRepository(medsDao: MedsDao): MedsRepository {
        return MedsRepository(medsDao)
    }

    @Provides
    fun provideMedsLogRepository(
        medsLogDao: MedsLogDao,
        medsLogFirestoreRepository: MedsLogFirestoreRepository
    ): MedsLogRepository {
        return MedsLogRepository(medsLogDao, medsLogFirestoreRepository)
    }

    @Provides
    fun provideMedsLogFirestoreRep(): MedsLogFirestoreRepository{
        return MedsLogFirestoreRepository()
    }

    @Provides
    fun provideMetadataRepository(metadataDao: MetadataDao): MetadataRepository {
        return MetadataRepository(metadataDao)
    }

    @Provides
    fun provideAlarmRepository(
        alarmDao: AlarmDao,
        alarmFirestoreRepository: AlarmFirestoreRepository
    ): AlarmRepository {
        return AlarmRepository(alarmDao, alarmFirestoreRepository)
    }

    @Provides
    fun provideAlarmFirestoreRep(): AlarmFirestoreRepository{
        return AlarmFirestoreRepository()
    }

    @Provides
    fun provideUserMedsRepository(
        userMedsDao: UserMedsDao,
        userMedsFirestoreRepository: UserMedsFirestoreRepository
    ): UserMedsRepository {
        return UserMedsRepository(userMedsDao, userMedsFirestoreRepository)
    }

    @Provides
    fun provideUserMedsFirestoreRep(): UserMedsFirestoreRepository{
        return UserMedsFirestoreRepository()
    }
}