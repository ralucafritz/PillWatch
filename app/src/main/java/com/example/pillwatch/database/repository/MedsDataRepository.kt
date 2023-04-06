package com.example.pillwatch.database.repository


import androidx.lifecycle.LiveData
import com.example.pillwatch.database.dao.MedsDataDao
import com.example.pillwatch.database.entity.MedsDataEntity

class MedsDataRepository(private val medsDataDao: MedsDataDao) {


    fun insertAll(dataList: List<MedsDataEntity>) {
            medsDataDao.insertAll(dataList)
    }

    fun getAllMeds() : List<MedsDataEntity> {
            return medsDataDao.getAllMeds()
    }

    fun clear() {
        medsDataDao.clear()
    }

}