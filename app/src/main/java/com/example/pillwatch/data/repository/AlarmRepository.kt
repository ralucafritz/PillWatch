package com.example.pillwatch.data.repository

import androidx.lifecycle.LiveData
import com.example.pillwatch.data.source.local.AlarmDao
import com.example.pillwatch.data.model.AlarmEntity

class AlarmRepository(private val alarmDao: AlarmDao){

    fun insert(alarm: AlarmEntity): Long {
        return alarmDao.insert(alarm)
    }

    fun insertAll(alarmList: List<AlarmEntity>) {
        alarmDao.insertAll(alarmList)
    }

    fun updateAlarm(alarm: AlarmEntity) {
        alarmDao.updateAlarm(alarm.id, alarm.timeInMillis, alarm.isEnabled)
    }

    fun clearForMedId(medId: Long) {
        alarmDao.clearForMedId(medId)
    }

    fun getAlarmsByMedId(medId: Long) : List<AlarmEntity> {
        return alarmDao.getAlarmsByMedId(medId)
    }

    fun clear() {
        alarmDao.clear()
    }

}