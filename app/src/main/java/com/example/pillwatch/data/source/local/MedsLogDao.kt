package com.example.pillwatch.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pillwatch.data.model.MedsLogEntity
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.utils.TakenStatus

@Dao
interface MedsLogDao {
    @Insert
    fun insert(medsLog: MedsLogEntity)

    @Query("SELECT * FROM meds_log_table WHERE medId = :medId")
    fun getLogByMedId(medId: String): List<MedsLogEntity>

    @Query("SELECT * FROM meds_log_table WHERE medId = :medId AND timestamp >= :startTime AND timestamp < :endTime")
    fun getLogInTimeframeByMedId(medId: String, startTime: Long, endTime: Long): List<MedsLogEntity>

    @Query("SELECT COUNT(*) FROM meds_log_table WHERE medId = :medId AND status = :status")
    fun getLogCountByStatus(medId: String, status: TakenStatus): Int

    @Query("SELECT * FROM meds_log_table WHERE id = :id")
    fun getMedsLogById(id: String): MedsLogEntity

    @Query("DELETE FROM meds_log_table")
    fun clear()
}