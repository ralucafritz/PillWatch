package com.example.pillwatch.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pillwatch.data.model.UserMedsEntity

@Dao
interface UserMedsDao {
    @Insert
    fun insert(userMed: UserMedsEntity)

    @Insert
    fun insertAll(userMedList: List<UserMedsEntity>)

    @Query("UPDATE user_meds_table SET isArchived = :isArchived WHERE id = :id")
    fun archiveMed(id: String, isArchived: Boolean)

    @Query("SELECT * FROM user_meds_table WHERE userId = :userId")
    fun getMedsForUserId(userId: String): List<UserMedsEntity>

    @Query("SELECT isArchived FROM user_meds_table WHERE id = :medId")
    fun isArchived(medId: String): Boolean

    @Query("SELECT * FROM user_meds_table WHERE userId = :userId AND isArchived = :isArchived")
    fun getAllNonArchivedMedsForUser(userId: String, isArchived: Boolean = false): List<UserMedsEntity>

    @Query("SELECT medId FROM user_meds_table WHERE userId = :userId")
    fun getMedIdForUserMedsByUserId(userId: String): List<Long?>

    @Query("SELECT * FROM user_meds_table WHERE id = :id")
    fun getMedById(id: String): UserMedsEntity

    @Query("SELECT COUNT(*) FROM user_meds_table WHERE userId = :userId and isArchived = :isArchived")
    fun getActiveMedCountByUserId(userId: String, isArchived: Boolean= false): Int?

    @Query("DELETE FROM user_meds_table WHERE id  = :id")
    fun deleteById(id: String)

    @Update
    fun update(medEntity: UserMedsEntity)

    @Query("DELETE FROM user_meds_table")
    fun clear()
}