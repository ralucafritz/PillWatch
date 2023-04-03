package com.example.pillwatch.database.repository


import androidx.lifecycle.LiveData
import com.example.pillwatch.database.dao.DatabaseDao
import com.example.pillwatch.database.entity.MedsDataEntity

class MedsDataRepository(private val databaseDao: DatabaseDao) {


    fun insertAll(dataList: List<MedsDataEntity>) {
            databaseDao.insertAll(dataList)
    }

    fun getAllMeds() : LiveData<List<MedsDataEntity>> {
            return databaseDao.getAllMeds()
    }
}