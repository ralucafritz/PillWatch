package com.example.pillwatch.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "alarms",
    foreignKeys = [ForeignKey(
        entity = UserMedsEntity::class,
        parentColumns = ["id"],
        childColumns = ["userMedsId"]
    )]
)
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userMedsId: Long,
    val hour: Int
) {
}