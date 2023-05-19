package com.example.pillwatch.data.repository


import androidx.lifecycle.LiveData
import com.example.pillwatch.data.source.local.MedsDao
import com.example.pillwatch.data.model.MedsEntity

class MedsRepository(private val medsDao: MedsDao) {

    fun insertAll(dataList: List<MedsEntity>) {
        medsDao.insertAll(dataList)
    }

    fun getAllMeds(): LiveData<List<MedsEntity>> {
        return medsDao.getAllMeds()
    }

    fun getMedsCount(): Int {
        return medsDao.getMedsCount()
    }

    fun searchMedsWithName(medName: String): List<MedsEntity> {
        return medsDao.searchMedsWithName(medName)
    }

    fun getRxCuiForMed(medId: Long): String {
        return medsDao.getRxCuiForMed(medId)
    }

    fun clear() {
        medsDao.clear()
    }

}