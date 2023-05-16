package com.example.pillwatch.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pillwatch.data.model.UserMedsEntity

@Dao
interface UserMedsDao {
    @Insert
    fun insert(userMed: UserMedsEntity): Long

    @Insert
    fun insertAll(userMedList: List<UserMedsEntity>)

    @Query("SELECT * FROM user_meds_table WHERE userId = :userId")
    fun getMedsForUserId(userId: Long): List<UserMedsEntity>

    @Query("SELECT medId FROM user_meds_table WHERE userId = :userId")
    fun getMedIdForMedsForUser(userId: Long): List<Long?>

    @Query("SELECT * FROM user_meds_table WHERE id = :id")
    fun getMedById(id: Long): UserMedsEntity

    @Query("DELETE FROM user_meds_table WHERE id  = :id")
    fun deleteById(id: Long)

    @Update
    fun update(medEntity: UserMedsEntity)

    @Query("DELETE FROM user_meds_table")
    fun clear()
}