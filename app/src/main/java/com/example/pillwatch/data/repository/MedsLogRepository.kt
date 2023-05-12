package com.example.pillwatch.data.repository

import androidx.lifecycle.LiveData
import com.example.pillwatch.data.source.local.MedsLogDao
import com.example.pillwatch.data.model.MedsLogEntity

class MedsLogRepository(private val medsLogDao: MedsLogDao) {

    fun insert(medsLog: MedsLogEntity) : Long {
        return medsLogDao.insert(medsLog)
    }

    fun getLogByMedId (medId: Long) : LiveData<List<MedsLogEntity?>> {
        return medsLogDao.getLogByMedId(medId)
    }

    fun getLogInTimeframeByMedId(medId: Long, startTime: Long, endTime: Long): List<MedsLogEntity> {
        return medsLogDao.getLogInTimeframeByMedId(medId, startTime, endTime)
    }

    fun clear() {
        medsLogDao.clear()
    }

}