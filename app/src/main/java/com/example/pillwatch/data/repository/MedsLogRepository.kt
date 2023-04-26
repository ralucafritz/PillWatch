package com.example.pillwatch.data.repository

import androidx.lifecycle.LiveData
import com.example.pillwatch.data.datasource.local.MedsLogDao
import com.example.pillwatch.data.model.MedsEntity
import com.example.pillwatch.data.model.MedsLogEntity

class MedsLogRepository(private val medsLogDao: MedsLogDao) {

    fun insert(medsLog: MedsLogEntity) : Long {
        return medsLogDao.insert(medsLog)
    }

    fun getLogByMedId (medId: Long) : LiveData<List<MedsLogEntity?>> {
        return medsLogDao.getLogByMedId(medId)
    }

    fun clear() {
        medsLogDao.clear()
    }

}