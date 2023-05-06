package com.example.pillwatch.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pillwatch.data.model.AlarmEntity

@Dao
interface AlarmDao {

    @Insert
    fun insert(alarm: AlarmEntity): Long

    @Insert
    fun insertAll(alarmList: List<AlarmEntity>)

    @Query("UPDATE alarms_table SET timeInMillis = :timeInMillis, isEnabled = :isEnabled  WHERE id = :id ")
    fun updateAlarm(id: Long, timeInMillis: Long, isEnabled: Boolean)

    @Query("DELETE FROM alarms_table WHERE medId = :medId")
    fun clearForMedId(medId: Long)

    @Query("SELECT * FROM alarms_table WHERE id = :alarmId")
    fun getAlarmById(alarmId: Long): AlarmEntity

    @Query("SELECT * FROM alarms_table WHERE medId = :medId ORDER BY timeInMillis DESC LIMIT 1")
    fun getLastAlarmByMedId(medId: Long): AlarmEntity

    @Query("SELECT * FROM alarms_table WHERE medId = :medId")
    fun getAlarmsByMedId(medId: Long): List<AlarmEntity>

    @Query("SELECT * FROM alarms_table")
    fun getAllAlarms(): List<AlarmEntity>

    @Query("DELETE FROM alarms_table")
    fun clear()
}