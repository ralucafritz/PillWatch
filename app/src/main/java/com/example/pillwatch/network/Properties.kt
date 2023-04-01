package com.example.pillwatch.network

data class MedsDataShaProperty (
    val file: String,
    val sha: String
)

data class MedDataProperty(
    val cimCode: String,
    val tradeName: String,
    val dci: String,
    val dosageForm: String,
    val concentration: String,
    val atcCode: String,
    val prescriptionType: String,
    val packageVolume: String,
    val lastUpdateDate: String,
    val rxCui: String
)
