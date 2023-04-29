package com.example.pillwatch.data.repository

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.example.pillwatch.data.datasource.local.UserMedsDao
import com.example.pillwatch.data.model.MedsEntity
import com.example.pillwatch.data.model.UserMedsEntity

class UserMedsRepository(private val userMedsDao: UserMedsDao) {

    fun insert(userMed: UserMedsEntity): Long{
        return userMedsDao.insert(userMed)
    }

    fun insertAll(userMedsList: List<UserMedsEntity>) {
        userMedsDao.insertAll(userMedsList)
    }

    fun getAllMedsForUser(userId: Long) : List<UserMedsEntity>{
        return userMedsDao.getMedsForUserId(userId)
    }

    fun getMedIdForMedsForUser(userId: Long): List<Long?> {
        return userMedsDao.getMedIdForMedsForUser(userId)
    }


    fun clear() {
        userMedsDao.clear()
    }

}