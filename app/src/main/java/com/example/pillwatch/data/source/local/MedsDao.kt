package com.example.pillwatch.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pillwatch.data.model.MedsEntity

@Dao
interface MedsDao {
    @Insert
    fun insertAll(medDataList: List<MedsEntity>)

    @Update
    fun update(medData: MedsEntity)

    @Query("DELETE FROM meds_table")
    fun clear()

    @Query("SELECT * FROM meds_table ORDER BY id ASC")
    fun getAllMeds(): LiveData<List<MedsEntity>>

    @Query("SELECT * FROM meds_table WHERE trade_name LIKE '%' ||  :medName || '%' ")
    fun searchMedsWithName(medName: String): List<MedsEntity>

    @Query("SELECT rxcui FROM meds_table WHERE id = :medId")
    fun getRxCuiForMed(medId: Long): String

    @Query("SELECT * FROM meds_table WHERE trade_name LIKE :inputText")
    fun getMedsWithSimilarName(inputText: String): LiveData<List<MedsEntity>>
}