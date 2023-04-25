package com.example.pillwatch.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_meds_table",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"]
    ), ForeignKey(
        entity = MedsEntity::class,
        parentColumns = ["id"],
        childColumns = ["medId"]
    )]
)
data class UserMedsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val tradeName: String,
    val userId: Long,
    val medId: Long? = null,
) {
}