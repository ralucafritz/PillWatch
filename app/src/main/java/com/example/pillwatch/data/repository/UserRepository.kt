package com.example.pillwatch.data.repository

import com.example.pillwatch.data.source.local.UserDao
import com.example.pillwatch.data.model.UserEntity
import com.example.pillwatch.utils.Role
import com.example.pillwatch.utils.hashPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class UserRepository(
    private val userDao: UserDao,
    private val userFirestoreRepository: UserFirestoreRepository
) {

    suspend fun insert(
        email: String,
        password: String,
        addToFirestore: Boolean = false
    ): UserEntity {
        return withContext(Dispatchers.IO) {
            val hashedUser =
                UserEntity(
                    UUID.randomUUID().toString(),
                    email,
                    null,
                    password,
                    null,
                    Role.USER
                )
            userDao.insert(hashedUser)

            if (addToFirestore) {
                userFirestoreRepository.addUser(hashedUser)
            }

            hashedUser
        }
    }

    suspend fun signup(
        email: String,
        password: String = "",
        addToFirestore: Boolean = false
    ): UserEntity? {
        return withContext(Dispatchers.IO) {
            val user = userDao.getUserByEmail(email)
            if (user != null) {
                if (password != "") {
                    null
                } else {
                    user
                }
            } else {
                val newPassword = if (password == "") {
                    UUID.randomUUID().toString()
                } else {
                    password
                }
                val hashedPassword = hashPassword(newPassword)
                insert(email, hashedPassword, addToFirestore)
            }
        }
    }

    suspend fun signup(
        userEntity: UserEntity
    ) {
        return withContext(Dispatchers.IO) {
            userDao.insert(userEntity)
        }
    }


    suspend fun getUserFromCloudByEmail(email: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            userFirestoreRepository.getUserByEmail(email)
        }
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByEmail(email)
        }
    }

    suspend fun getUserById(id: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(id)
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            userDao.clear()
            userFirestoreRepository.clearUsers()
        }
    }

    suspend fun deleteUser(userId: String) {
        withContext(Dispatchers.IO) {
            userFirestoreRepository.deleteUser(userId)
        }
    }

    suspend fun getIdByEmail(email: String): String? {
        return withContext(Dispatchers.IO) {
            userDao.getIdByEmail(email)
        }
    }

    suspend fun getRoleById(userId: String): Role? {
        return withContext(Dispatchers.IO) {
            userDao.getRoleById(userId)
        }
    }

    suspend fun getUserByIdToken(idToken: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByIdToken(idToken)
        }
    }

    suspend fun getUserNameById(id: String): String? {
        return withContext(Dispatchers.IO) {
            userDao.getUserNameById(id)
        }
    }

    suspend fun updateUsername(userId: String, username: String) {
        withContext(Dispatchers.IO) {
            userDao.updateUserName(userId, username)
            userFirestoreRepository.updateUsername(userId, username)
        }
    }

    suspend fun updateUserCloud(user: UserEntity) {
        withContext(Dispatchers.IO) {
            userFirestoreRepository.updateRole(user.id, user.role)
        }
    }
}