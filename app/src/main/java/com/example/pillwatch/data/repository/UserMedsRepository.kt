package com.example.pillwatch.data.repository

import com.example.pillwatch.data.source.local.UserMedsDao
import com.example.pillwatch.data.model.UserMedsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserMedsRepository(
    private val userMedsDao: UserMedsDao,
    private val userMedsFirestoreRepository: UserMedsFirestoreRepository
) {

    suspend fun insert(userMed: UserMedsEntity): String {
        return withContext(Dispatchers.IO) {
            userMedsFirestoreRepository.addUserMed(userMed)
            userMedsDao.insert(userMed)
            userMed.id
        }
    }

    fun insertAll(userMedsList: List<UserMedsEntity>) {
        userMedsDao.insertAll(userMedsList)
        userMedsList.forEach {
            userMedsFirestoreRepository.addUserMed(it)
        }
    }

    fun getAllMedsForUser(userId: String): List<UserMedsEntity> {
        return userMedsDao.getMedsForUserId(userId)
    }

    fun getMedIdForMedsForUser(userId: String): List<Long?> {
        return userMedsDao.getMedIdForUserMedsByUserId(userId)
    }

    fun getMedById(id: String): UserMedsEntity {
        return userMedsDao.getMedById(id)
    }

    fun getMedCountByUserId(userId: String): Int? {
        return userMedsDao.getMedCountByUserId(userId)
    }

    suspend fun deleteById(id: String) {
        withContext(Dispatchers.IO) {
            userMedsDao.deleteById(id)
            userMedsFirestoreRepository.deleteUserMeds(id)
        }
    }

    suspend fun updateMed(medEntity: UserMedsEntity) {
        withContext(Dispatchers.IO) {
            userMedsDao.update(medEntity)
            userMedsFirestoreRepository.updateUserMeds(medEntity)
        }
    }

    suspend fun clear() {
        return withContext(Dispatchers.IO) {
            userMedsDao.clear()
            userMedsFirestoreRepository.cleanUserMeds()
        }
    }

    suspend fun getUserMedsFromCloudByUserId(userId: String): List<UserMedsEntity?> {
        return withContext(Dispatchers.IO) {
            userMedsFirestoreRepository.getUserMedsByUserId(userId)
        }
    }

}