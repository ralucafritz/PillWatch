package com.example.pillwatch.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pillwatch.data.model.MedsLogEntity

@Dao
interface MedsLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(medsLog: MedsLogEntity): Long

    @Query("SELECT * FROM meds_log_table WHERE medId = :medId")
    fun getLogByMedId(medId: Long): LiveData<List<MedsLogEntity?>>

    @Query("DELETE FROM meds_log_table")
    fun clear()
}