package com.example.pillwatch.di

import android.content.Context
import com.example.pillwatch.alarms.AlarmGenerator
import com.example.pillwatch.alarms.AlarmHandler
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.MedsLogRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AlarmsModule {
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
        medsLogRepository: MedsLogRepository
    ): AlarmHandler {
        return AlarmHandler(context, alarmGenerator, alarmRepository, medsLogRepository)
    }
}