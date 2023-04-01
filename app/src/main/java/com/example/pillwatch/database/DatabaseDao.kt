package com.example.pillwatch.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DatabaseDao {
    @Insert
    fun insert(medData: MedDataEntity)

    @Update
    fun update(medData: MedDataEntity)

    @Query("DELETE FROM med_data_table")
    fun clear()

    @Query("SELECT * FROM med_data_table ODER BY medId ASC")
    fun getAllMeds(): LiveData<List<MedDataEntity>>
}