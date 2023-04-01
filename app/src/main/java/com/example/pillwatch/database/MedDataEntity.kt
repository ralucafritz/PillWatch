package com.example.pillwatch.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "med_data_table")
data class MedDataEntity(
    @PrimaryKey(autoGenerate = true)
    var medId: Long = 0L,

    @ColumnInfo(name = "CIM Code")
    val cimCode: String = "",

    @ColumnInfo(name = "Trade name")
    val tradeName: String = "",

    @ColumnInfo(name = "DCI")
    val dci: String = "",

    @ColumnInfo(name = "Dosage Form")
    val dosageForm: String = "",

    @ColumnInfo(name = "Concentration")
    val concentration: String = "",

    @ColumnInfo(name = "ATC Code")
    val atcCode: String = "",

    @ColumnInfo(name = "Prescription Type")
    val prescriptionType: String = "",

    @ColumnInfo(name = "Package volume")
    val packageVolume: String = "",

    @ColumnInfo(name = "Last Update Date")
    val lastUpdateDate: String = "",

    @ColumnInfo(name = "RxCui")
    val rxCui: String = ""
)
