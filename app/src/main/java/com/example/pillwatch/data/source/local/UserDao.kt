package com.example.pillwatch.data.source.local

import androidx.room.*
import com.example.pillwatch.data.model.UserEntity
import com.example.pillwatch.utils.Role

@Dao
interface UserDao {
    @Insert
    fun insert(user: UserEntity)

    @Query("SELECT * FROM users_table WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): UserEntity?

    @Query("DELETE FROM users_table")
    fun clear()

    @Query("SELECT id FROM users_table WHERE email =:email")
    fun getIdByEmail(email: String): String?

    @Query("SELECT role FROM users_table WHERE id = :userId")
    fun getRoleById(userId: String): Role

    @Query("SELECT * FROM users_table WHERE idToken =:idToken")
    fun getUserByIdToken(idToken: String): UserEntity?

    @Query("SELECT username FROM users_table WHERE id =:id")
    fun getUserNameById(id: String): String?

    @Query("SELECT * FROM users_table WHERE id = :id")
    fun getUserById(id: String) : UserEntity?

    @Query("UPDATE users_table SET username = :newName WHERE id = :userId")
    fun updateUserName(userId: String, newName: String)

    @Query("UPDATE users_table SET role = :newRole WHERE id = :userId")
    fun updateUserRole(userId: String, newRole: Role)
}