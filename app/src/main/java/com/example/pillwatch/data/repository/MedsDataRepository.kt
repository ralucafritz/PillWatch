package com.example.pillwatch.data.repository


import com.example.pillwatch.data.datasource.local.MedsDataDao
import com.example.pillwatch.data.model.MedsDataEntity

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