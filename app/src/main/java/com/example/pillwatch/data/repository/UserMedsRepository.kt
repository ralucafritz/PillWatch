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

    suspend fun insertAll(userMedsList: List<UserMedsEntity>) {
        return withContext(Dispatchers.IO) {
            userMedsDao.insertAll(userMedsList)
            userMedsList.forEach {
                userMedsFirestoreRepository.addUserMed(it)
            }
        }
    }

    suspend fun archiveMed(id: String, isArchived: Boolean) {
        withContext(Dispatchers.IO) {
            userMedsDao.archiveMed(id, isArchived)
            val userMed = userMedsDao.getMedById(id)
            userMedsFirestoreRepository.updateUserMed(userMed)
        }
    }

    fun getAllMedsForUser(userId: String): List<UserMedsEntity> {
        return userMedsDao.getMedsForUserId(userId)
    }

    fun isArchived(medId: String): Boolean {
        return userMedsDao.isArchived(medId)
    }

    fun getAllNonArchivedMedsForUser(userId: String): List<UserMedsEntity> {
        return userMedsDao.getAllNonArchivedMedsForUser(userId)
    }

    fun getMedIdForMedsForUser(userId: String): List<Long?> {
        return userMedsDao.getMedIdForUserMedsByUserId(userId)
    }

    fun getMedById(id: String): UserMedsEntity {
        return userMedsDao.getMedById(id)
    }

    fun getActiveMedCountByUserId(userId: String): Int? {
        return userMedsDao.getActiveMedCountByUserId(userId)
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
            userMedsFirestoreRepository.updateUserMed(medEntity)
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