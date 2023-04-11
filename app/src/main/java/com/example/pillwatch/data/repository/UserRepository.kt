package com.example.pillwatch.data.repository

import com.example.pillwatch.data.datasource.local.UserDao
import com.example.pillwatch.data.model.UserEntity
import com.example.pillwatch.utils.FirebaseUtils.firebaseAuth
import com.example.pillwatch.utils.hashPassword
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import timber.log.Timber
import java.util.*

class UserRepository(private val userDao: UserDao) {

    fun insert(user: UserEntity): UserEntity? {
        val hashedPassword = hashPassword(user.password)
        val hashedUser = UserEntity(user.id, user.email, hashedPassword)
        userDao.insert(hashedUser)
        return hashedUser
    }

    fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    fun clear() {
        userDao.clear()
    }
}