package com.example.pillwatch.data.repository


import androidx.lifecycle.LiveData
import androidx.room.util.query
import com.example.pillwatch.data.datasource.local.MedsDao
import com.example.pillwatch.data.model.MedsEntity
import timber.log.Timber

class MedsRepository(private val medsDao: MedsDao) {

    fun insertAll(dataList: List<MedsEntity>) {
            medsDao.insertAll(dataList)
    }

    fun getAllMeds() : LiveData<List<MedsEntity>> {
            return medsDao.getAllMeds()
    }

    fun searchMedsWithName(medName: String): List<String> {
        val queryResult =  medsDao.searchMedsWithName(medName)
        Timber.d(queryResult.toString())
        return queryResult
    }

    fun clear() {
        medsDao.clear()
    }

}