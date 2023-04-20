package com.example.pillwatch.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meds_table")
data class MedsEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    @ColumnInfo(name = "trade_name")
    val tradeName: String = "",

    @ColumnInfo(name = "dci")
    val dci: String = "",

    @ColumnInfo(name = "dosage_form")
    val dosageForm: String = "",

    @ColumnInfo(name = "concentration")
    val concentration: String = "",

    @ColumnInfo(name = "atc_code")
    val atcCode: String = "",

    @ColumnInfo(name = "last_update_date")
    val lastUpdateDate: String = "",

    @ColumnInfo(name = "rxcui")
    val rxCui: String = ""
)
