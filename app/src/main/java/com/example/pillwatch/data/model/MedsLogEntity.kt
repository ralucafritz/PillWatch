package com.example.pillwatch.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.pillwatch.utils.TakenStatus

@Entity(
    tableName = "user_meds_log",
    foreignKeys = [ForeignKey(
        entity = UserMedsEntity::class,
        parentColumns = ["id"],
        childColumns = ["userMedsId"]
    )]
)
data class MedsLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userMedsId: Long,
    val status: TakenStatus,
    val timestamp: Long
)