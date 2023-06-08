package com.example.pillwatch.data.repository

import androidx.lifecycle.LiveData
import com.example.pillwatch.data.source.local.AlarmDao
import com.example.pillwatch.data.model.AlarmEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class AlarmRepository(
    private val alarmDao: AlarmDao,
    private val alarmFirestoreRepository: AlarmFirestoreRepository
) {

    suspend fun insert(alarm: AlarmEntity): String {
        return withContext(Dispatchers.IO) {
            alarmDao.insert(alarm)
            alarm.id
        }
    }

    suspend fun insertAll(alarmList: List<AlarmEntity>) {
        withContext(Dispatchers.IO) {
            alarmDao.insertAll(alarmList)
            alarmList.forEach {
                alarmFirestoreRepository.addAlarm(it)
            }
        }
    }

    suspend fun updateAlarm(alarm: AlarmEntity) {
        withContext(Dispatchers.IO) {
            alarmDao.updateAlarm(alarm.id, alarm.timeInMillis, alarm.isEnabled)
            alarmFirestoreRepository.updateAlarm(alarm)
        }
    }

    suspend fun clearForMedId(medId: String): Deferred<Unit> = CoroutineScope(Dispatchers.IO).async {
        withContext(Dispatchers.IO) {
            alarmFirestoreRepository.deleteAlarm(medId).await()
            alarmDao.clearForMedId(medId)
        }
    }

    fun getAlarmById(alarmId: String): AlarmEntity {
        return alarmDao.getAlarmById(alarmId)
    }

    fun getLastAlarmByMedId(medId: String): AlarmEntity {
        return alarmDao.getLastAlarmByMedId(medId)
    }

    fun getAlarmsByMedId(medId: String): List<AlarmEntity> {
        return alarmDao.getAlarmsByMedId(medId)
    }

    fun getAllAlarms(): List<AlarmEntity> {
        return alarmDao.getAllAlarms()
    }

    fun getNextAlarmBeforeMidnight(
        medId: String,
        currentTimeInMillis: Long,
        midnightInMillis: Long
    ): AlarmEntity? {
        return alarmDao.getNextAlarmBeforeMidnight(medId, currentTimeInMillis, midnightInMillis)
    }

    suspend fun clear() {
        return withContext(Dispatchers.IO) {
            alarmDao.clear()
            alarmFirestoreRepository.cleanAlarms()
        }
    }

    suspend fun getAlarmsFromCloudByMedId(medId: String): List<AlarmEntity?> {
        return withContext(Dispatchers.IO) {
            alarmFirestoreRepository.getAlarmsByMedId(medId)
        }
    }
}