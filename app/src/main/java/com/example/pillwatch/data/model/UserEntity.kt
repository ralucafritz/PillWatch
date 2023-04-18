package com.example.pillwatch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class UserEntity (
    @PrimaryKey (autoGenerate = true) val id: Long,
    val email: String,
    val password: String,
    val idToken: String?

        )