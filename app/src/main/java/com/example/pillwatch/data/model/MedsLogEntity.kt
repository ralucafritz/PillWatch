package com.example.pillwatch.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.pillwatch.utils.TakenStatus

@Entity(
    tableName = "meds_log_table",
    foreignKeys = [ForeignKey(
        entity = UserMedsEntity::class,
        parentColumns = ["id"],
        childColumns = ["medId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("medId")]
)
data class MedsLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val medId: Long,
    val status: TakenStatus,
    val timestamp: Long
)