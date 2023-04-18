package com.example.pillwatch.data.repository

import com.example.pillwatch.data.datasource.local.UserDao
import com.example.pillwatch.data.model.UserEntity
import com.example.pillwatch.utils.hashPassword

class UserRepository(private val userDao: UserDao) {

    fun insert(user: UserEntity): UserEntity {
        val hashedPassword = hashPassword(user.password)
        val hashedUser = UserEntity(user.id, user.email, hashedPassword, user.idToken)
        userDao.insert(hashedUser)
        return hashedUser
    }

    fun insertGoogle(user: UserEntity, idToken: String): UserEntity {
        val newUser = UserEntity(user.id, user.email, user.password, idToken)
        return insert(newUser)
    }


    fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    fun clear() {
        userDao.clear()
    }

    fun getIdByEmail(email: String): Long? {
        return userDao.getIdByEmail(email)
    }

    fun getUserByIdToken(idToken: String): UserEntity? {
        return userDao.geUserByIdToken(idToken)
    }


}