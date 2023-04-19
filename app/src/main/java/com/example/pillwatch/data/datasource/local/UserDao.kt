package com.example.pillwatch.data.datasource.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pillwatch.data.model.UserEntity
import com.example.pillwatch.utils.Role

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserEntity)

    @Query("SELECT * FROM users_table WHERE email = :email")
    fun getUserByEmail(email: String): LiveData<UserEntity?>

    @Query("DELETE FROM users_table")
    fun clear()

    @Query("SELECT id FROM users_table WHERE email =:email")
    fun getIdByEmail(email: String): Long?

    @Query("SELECT * FROM users_table WHERE idToken =:idToken")
    fun geUserByIdToken(idToken: String): LiveData<UserEntity?>

    @Query("UPDATE users_table SET username = :newName WHERE id = :userId")
    fun updateUserName(userId: String, newName: String)

    @Query("UPDATE users_table SET role = :newRole WHERE id = :userId")
    fun updateUserRole(userId: String, newRole: Role)
}