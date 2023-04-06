package com.example.pillwatch.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metadata_table")
class MetadataEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name="Name")
    val name: String = "",

    @ColumnInfo(name = "value")
    val value: String = ""
    )