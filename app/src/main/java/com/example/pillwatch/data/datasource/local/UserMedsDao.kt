package com.example.pillwatch.data.datasource.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pillwatch.data.model.UserMedsEntity

@Dao
interface UserMedsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userMed: UserMedsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(userMedList : List<UserMedsEntity>)

    @Query("SELECT * FROM user_meds_table WHERE userId = :userId")
    fun getMedsForUserId(userId: Long): List<UserMedsEntity>

    @Query("SELECT medId FROM user_meds_table WHERE userId = :userId")
    fun getMedIdForMedsForUser(userId: Long): List<Long>

    @Query("DELETE FROM user_meds_table")
    fun clear()
}