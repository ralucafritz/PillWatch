package com.example.pillwatch.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "user_meds_table",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = MedsEntity::class,
        parentColumns = ["id"],
        childColumns = ["medId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userId"), Index("medId")]
)

data class UserMedsEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val tradeName: String = "",
    val userId: String = "",
    val medId: Long? = null,
    val concentration: String? = "",
    val isArchived: Boolean? = false
) {
}