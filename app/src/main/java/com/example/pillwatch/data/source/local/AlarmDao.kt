package com.example.pillwatch.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pillwatch.data.model.AlarmEntity

@Dao
interface AlarmDao {

    @Insert
    fun insert(alarm: AlarmEntity)

    @Insert
    fun insertAll(alarmList: List<AlarmEntity>)

    @Query("UPDATE alarms_table SET timeInMillis = :timeInMillis, isEnabled = :isEnabled  WHERE id = :id ")
    fun updateAlarm(id: String, timeInMillis: Long, isEnabled: Boolean)

    @Query("DELETE FROM alarms_table WHERE medId = :medId")
    fun clearForMedId(medId: String)

    @Query("SELECT * FROM alarms_table WHERE id = :alarmId")
    fun getAlarmById(alarmId: String): AlarmEntity

    @Query("SELECT * FROM alarms_table WHERE medId = :medId ORDER BY timeInMillis DESC LIMIT 1")
    fun getLastAlarmByMedId(medId: String): AlarmEntity

    @Query("SELECT * FROM alarms_table WHERE medId = :medId")
    fun getAlarmsByMedId(medId: String): List<AlarmEntity>

    @Query("SELECT * FROM alarms_table")
    fun getAllAlarms(): List<AlarmEntity>

    @Query("SELECT * FROM alarms_table WHERE medId = :medId AND timeInMillis > :currentTimeInMillis AND timeInMillis < :midnightInMillis ORDER BY timeInMillis ASC LIMIT 1")
    fun getNextAlarmBeforeMidnight(medId: String, currentTimeInMillis: Long, midnightInMillis: Long): AlarmEntity?

    @Query("DELETE FROM alarms_table")
    fun clear()
}