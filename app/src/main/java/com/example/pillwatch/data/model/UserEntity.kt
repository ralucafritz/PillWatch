package com.example.pillwatch.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pillwatch.utils.Role
import java.util.UUID

@Entity(tableName = "users_table")
data class UserEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name= "id")
    val id: Long = 0L,

    @ColumnInfo(name= "email")
    val email: String,

    @ColumnInfo(name= "username")
    val username: String?,

    @ColumnInfo(name= "password")
    val password: String,

    @ColumnInfo(name= "idToken")
    val idToken: String?,

    @ColumnInfo(name= "role")
    val role: Role
    )
