package com.example.pillwatch.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "med_data_table")
data class MedsDataEntity(
    @PrimaryKey(autoGenerate = true)
    var medId: Long = 0L,

    @ColumnInfo(name = "cim_code")
    val cimCode: String = "",

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

    @ColumnInfo(name = "prescription_type")
    val prescriptionType: String = "",

    @ColumnInfo(name = "package_volume")
    val packageVolume: String? = "",

    @ColumnInfo(name = "last_update_date")
    val lastUpdateDate: String = "",

    @ColumnInfo(name = "rxcui")
    val rxCui: String = ""
)
