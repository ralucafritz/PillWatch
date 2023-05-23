package com.example.pillwatch.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.pillwatch.utils.AlarmTiming
import java.util.UUID

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
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val medId: String = "",
    var timeInMillis: Long = 0L,
    var alarmTiming: AlarmTiming = AlarmTiming.NO_REMINDERS,
    var isEnabled: Boolean = true,
    var everyXHours: Int = 4
) {
}