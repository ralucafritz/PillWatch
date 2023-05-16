package com.example.pillwatch.data.repository

import com.example.pillwatch.data.source.local.UserMedsDao
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

    fun getMedById(id: Long): UserMedsEntity {
        return userMedsDao.getMedById(id)
    }

    fun deleteById(id: Long) {
        userMedsDao.deleteById(id)
    }

    fun updateMed(medEntity: UserMedsEntity) {
        userMedsDao.update(medEntity)
    }

    fun clear() {
        userMedsDao.clear()
    }

}