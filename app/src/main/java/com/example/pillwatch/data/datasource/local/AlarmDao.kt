package com.example.pillwatch.data.datasource.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pillwatch.data.model.AlarmEntity

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarm: AlarmEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(alarmList: List<AlarmEntity>)

    @Query("SELECT * FROM alarms_table WHERE medId = :medId")
    fun getAlarmsByMedId(medId: Long) : LiveData<List<AlarmEntity?>>

    @Query("DELETE FROM alarms_table")
    fun clear()
}