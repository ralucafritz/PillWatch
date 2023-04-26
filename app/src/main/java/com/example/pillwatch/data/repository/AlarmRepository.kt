package com.example.pillwatch.data.repository

import androidx.lifecycle.LiveData
import com.example.pillwatch.data.datasource.local.AlarmDao
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.model.MedsEntity

class AlarmRepository(private val alarmDao: AlarmDao){

    fun insert(alarm: AlarmEntity): Long {
        return alarmDao.insert(alarm)
    }

    fun insertAll(alarmList: List<AlarmEntity>) {
        alarmDao.insertAll(alarmList)
    }

    fun getAlarmsByMedId(medId: Long) : LiveData<List<AlarmEntity?>> {
        return alarmDao.getAlarmsByMedId(medId)
    }

    fun clear() {
        alarmDao.clear()
    }

}