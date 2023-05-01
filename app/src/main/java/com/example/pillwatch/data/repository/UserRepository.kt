package com.example.pillwatch.data.repository

import android.provider.SyncStateContract.Helpers.insert
import com.example.pillwatch.data.source.local.UserDao
import com.example.pillwatch.data.model.UserEntity
import com.example.pillwatch.utils.Role
import com.example.pillwatch.utils.hashPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun insert(email: String, password: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            val hashedUser =
                UserEntity(
                    0L,
                    email,
                    null,
                    password,
                    null,
                    Role.USER
                )
            val id = userDao.insert(hashedUser)
            hashedUser.copy(id = id)
        }

    }

    suspend fun signup(email: String, password: String = "") : UserEntity?{
        return withContext(Dispatchers.IO) {
            val user = userDao.getUserByEmail(email)
            if (user != null  ) {
                if(password!=""){
                    null
                }
                else {
                    user
                }
            } else {
                val hashedPassword = hashPassword(password)
                insert(email, hashedPassword)
            }
        }
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByEmail(email)
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            userDao.clear()
        }
    }

    suspend fun getIdByEmail(email: String): Long? {
        return withContext(Dispatchers.IO) {
            userDao.getIdByEmail(email)
        }
    }

    suspend fun getUserByIdToken(idToken: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByIdToken(idToken)
        }
    }

    suspend fun getUserNameById(id: Long): String? {
        return withContext(Dispatchers.IO) {
            userDao.getUserNameById(id)
        }
    }

    suspend fun updateUserName(userId: Long, newName: String) {
        withContext(Dispatchers.IO) {
            userDao.updateUserName(userId, newName)
        }

    }

    suspend fun updateUserRole(userId: Long, newRole: Role) {
        withContext(Dispatchers.IO) {
            userDao.updateUserRole(userId, newRole)
        }
    }

}