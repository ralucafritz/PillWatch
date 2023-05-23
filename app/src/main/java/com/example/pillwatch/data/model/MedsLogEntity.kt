package com.example.pillwatch.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.pillwatch.utils.TakenStatus
import java.util.UUID

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
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val medId: String = "",
    val status: TakenStatus = TakenStatus.MISSED,
    val timestamp: Long = 0L
)