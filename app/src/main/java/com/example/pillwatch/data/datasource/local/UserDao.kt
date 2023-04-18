package com.example.pillwatch.data.datasource.local

import androidx.room.*
import com.example.pillwatch.data.model.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserEntity)

    @Query("SELECT * FROM users_table WHERE email = :email")
    fun getUserByEmail(email: String): UserEntity?

    @Query("DELETE FROM users_table")
    fun clear()

    @Query("SELECT id FROM users_table WHERE email =:email")
    fun getIdByEmail(email: String): Long?

    @Query("SELECT * FROM users_table WHERE idToken =:idToken")
    fun geUserByIdToken(idToken: String): UserEntity?

}