package com.example.pillwatch.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pillwatch.database.entity.MedsDataEntity

@Dao
interface DatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(medDataList: List<MedsDataEntity>)

    @Update
    fun update(medData: MedsDataEntity)

    @Query("DELETE FROM med_data_table")
    fun clear()

    @Query("SELECT * FROM med_data_table ORDER BY medId ASC")
    fun getAllMeds(): LiveData<List<MedsDataEntity>>

    @Query("SELECT last_update_date FROM med_data_table LIMIT 1")
    fun getLastUpdateDate(): LiveData<String>
}