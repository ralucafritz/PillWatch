package com.example.pillwatch.data.repository


import androidx.lifecycle.LiveData
import com.example.pillwatch.data.datasource.local.MedsDao
import com.example.pillwatch.data.model.MedsEntity

class MedsDataRepository(private val medsDao: MedsDao) {


    fun insertAll(dataList: List<MedsEntity>) {
            medsDao.insertAll(dataList)
    }

    fun getAllMeds() : LiveData<List<MedsEntity>> {
            return medsDao.getAllMeds()
    }

    fun clear() {
        medsDao.clear()
    }

}