package com.example.pillwatch.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alarms_table",
    foreignKeys = [ForeignKey(
        entity = UserMedsEntity::class,
        parentColumns = ["id"],
        childColumns = ["medId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("medId")]
)
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val medId: Long,
    var timeInMillis: Long,
    var isEnabled: Boolean
) {
}