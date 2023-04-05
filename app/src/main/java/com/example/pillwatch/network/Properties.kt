package com.example.pillwatch.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class MedsDataShaProperty (
    val file: List<MedsDataProperty>,
    val sha: String
)

@JsonClass(generateAdapter = true)
data class MedsDataProperty(
    @Json(name ="CIM Code") val cimCode: String,
    @Json(name ="Trade name")val tradeName: String,
    @Json(name ="DCI") val dci: String,
    @Json(name ="Dosage Form") val dosageForm: String,
    @Json( name ="Concentration") val concentration: String,
    @Json(name = "ATC Code") val atcCode: String,
    @Json(name ="Prescription Type") val prescriptionType: String,
    @Json(name ="Package volume") val packageVolume: String?,
    @Json(name ="Last Update Date") val lastUpdateDate: String,
    @Json(name ="RxCui") val rxCui: String
)
