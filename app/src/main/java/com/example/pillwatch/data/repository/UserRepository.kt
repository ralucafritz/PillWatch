package com.example.pillwatch.data.repository

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.example.pillwatch.data.datasource.local.UserDao
import com.example.pillwatch.data.model.UserEntity
import com.example.pillwatch.utils.Role
import com.example.pillwatch.utils.hashPassword

class UserRepository(private val userDao: UserDao) {

    fun insert(user: UserEntity): UserEntity {
        val hashedPassword = hashPassword(user.password)
        val hashedUser = UserEntity(user.id, user.email, user.username, hashedPassword, user.idToken, user.role)
        userDao.insert(hashedUser)
        return hashedUser
    }

    fun getUserByEmail(email: String): LiveData<UserEntity?> {
        return userDao.getUserByEmail(email)
    }

    fun clear() {
        userDao.clear()
    }

    fun getIdByEmail(email: String): Long? {
        return userDao.getIdByEmail(email)
    }

    fun getUserByIdToken(idToken: String): LiveData<UserEntity?> {
        return userDao.getUserByIdToken(idToken)
    }

    fun getUserNameById(id: Long) : String? {
        return userDao.getUserNameById(id)
    }

    fun updateUserName(userId: String, newName: String) {
        userDao.updateUserName(userId, newName)
    }

    fun updateUserRole(userId: String, newRole: Role) {
        userDao.updateUserRole(userId, newRole)
    }

}