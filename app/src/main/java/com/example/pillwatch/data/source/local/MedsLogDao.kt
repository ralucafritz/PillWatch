package com.example.pillwatch.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pillwatch.data.model.MedsLogEntity
import com.example.pillwatch.utils.TakenStatus

@Dao
interface MedsLogDao {
    @Insert
    fun insert(medsLog: MedsLogEntity): Long

    @Query("SELECT * FROM meds_log_table WHERE medId = :medId")
    fun getLogByMedId(medId: Long): List<MedsLogEntity>

    @Query("SELECT * FROM meds_log_table WHERE medId = :medId AND timestamp >= :startTime AND timestamp < :endTime")
    fun getLogInTimeframeByMedId(medId: Long, startTime: Long, endTime: Long): List<MedsLogEntity>

    @Query("SELECT COUNT(*) FROM meds_log_table WHERE medId = :medId AND status = :status")
    fun getLogCountByStatus(medId: Long, status: TakenStatus): Long

    @Query("DELETE FROM meds_log_table")
    fun clear()
}