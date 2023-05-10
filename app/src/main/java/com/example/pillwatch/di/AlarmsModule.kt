package com.example.pillwatch.di

import android.content.Context
import com.example.pillwatch.alarms.AlarmGenerator
import com.example.pillwatch.alarms.AlarmHandler
import com.example.pillwatch.alarms.AlarmScheduler
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.MedsLogRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AlarmsModule {
    @Provides
    @Singleton
    fun provideAlarmSchedule(context: Context): AlarmScheduler {
        return AlarmScheduler(context)
    }

    @Provides
    @Singleton
    fun provideAlarmsGenerator(): AlarmGenerator {
        return AlarmGenerator()
    }

    @Provides
    @Singleton
    fun provideAlarmHandler(
        context: Context,
        alarmGenerator: AlarmGenerator,
        alarmRepository: AlarmRepository,
        medsLogRepository: MedsLogRepository,
        userMedsRepository: UserMedsRepository
    ): AlarmHandler {
        return AlarmHandler(context, alarmGenerator, alarmRepository, medsLogRepository, userMedsRepository)
    }
}