package com.example.pillwatch.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pillwatch.utils.Role
import com.google.firebase.encoders.annotations.Encodable
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.UUID

@Entity(tableName = "users_table")
data class UserEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "email")
    val email: String="",

    @ColumnInfo(name = "username")
    var username: String? =null,

    @Transient
    @ColumnInfo(name = "password")
    val password: String = "",

    @ColumnInfo(name = "idToken")
    val idToken: String? = null,

    @ColumnInfo(name = "role")
    val role: Role = Role.USER
)
