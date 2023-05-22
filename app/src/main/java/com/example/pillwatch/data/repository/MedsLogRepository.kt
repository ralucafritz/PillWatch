package com.example.pillwatch.data.repository

import androidx.lifecycle.LiveData
import com.example.pillwatch.data.source.local.MedsLogDao
import com.example.pillwatch.data.model.MedsLogEntity
import com.example.pillwatch.utils.TakenStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MedsLogRepository(
    private val medsLogDao: MedsLogDao,
    private val medsLogFirestoreRepository: MedsLogFirestoreRepository
) {

    suspend  fun insert(medsLog: MedsLogEntity): String {
        return withContext(Dispatchers.IO) {
            medsLogFirestoreRepository.addMedsLog(medsLog)
            medsLogDao.insert(medsLog)
            medsLog.id
        }
    }

    suspend fun getLogByMedId(medId: String): List<MedsLogEntity> {
        return withContext(Dispatchers.IO) {
            medsLogDao.getLogByMedId(medId)
        }
    }

    suspend fun getLogInTimeframeByMedId(medId: String, startTime: Long, endTime: Long): List<MedsLogEntity> {
        return withContext(Dispatchers.IO) {
            medsLogDao.getLogInTimeframeByMedId(medId, startTime, endTime)
        }
    }

    suspend fun getLogCountByStatus(medId: String, status: TakenStatus): Int {
        return withContext(Dispatchers.IO) {
            medsLogDao.getLogCountByStatus(medId, status)
        }
    }

    suspend fun clear() {
        return withContext(Dispatchers.IO) {
            medsLogFirestoreRepository.cleanMedsLogs()
            medsLogDao.clear()
        }
    }

    suspend fun getMedsLogsFromCloudByMedId(medId: String): List<MedsLogEntity?> {
        return withContext(Dispatchers.IO) {
            medsLogFirestoreRepository.getMedsLogsByMedId(medId)
        }
    }

}