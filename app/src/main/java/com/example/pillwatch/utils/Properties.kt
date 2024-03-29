package com.example.pillwatch.utils

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.firebase.auth.AuthResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

data class MedsDataShaProperty(
    val file: List<MedsDataProperty>,
    val sha: String
)

@JsonClass(generateAdapter = true)
data class MedsDataProperty(
    @Json(name = "Trade name") val tradeName: String,
    @Json(name = "DCI") val dci: String,
    @Json(name = "Dosage Form") val dosageForm: String,
    @Json(name = "Concentration") val concentration: String = "",
    @Json(name = "ATC Code") val atcCode: String,
    @Json(name = "Last Update Date") val lastUpdateDate: String,
    @Json(name = "RxCui") val rxCui: String
)

data class ValidationProperty(
    val isValid: Boolean,
    val message: String
)

data class AuthResultProperty(
    var success: Boolean,
    var result: AuthResult?,
    var email: String?
)

data class InteractionProperty(
    var rxCui1: String,
    var rxCui2: String,
    var severity: String
)

data class InteractionTestProperty(
    val interaction: List<List<InteractionProperty>>?
)