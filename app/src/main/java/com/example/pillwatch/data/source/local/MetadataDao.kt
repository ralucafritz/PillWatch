package com.example.pillwatch.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pillwatch.data.model.MetadataEntity

@Dao
interface MetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(metadata: MetadataEntity)

    @Update
    fun update(metadata: MetadataEntity)

    @Query("DELETE FROM metadata_table")
    fun clear()

    @Query("SELECT * FROM metadata_table ORDER BY id ASC")
    fun getAllMetadata(): LiveData<List<MetadataEntity>>

    @Query("SELECT * FROM metadata_table WHERE Name = :nameMetadata LIMIT 1")
    fun getMetadata(nameMetadata: String): MetadataEntity?
}