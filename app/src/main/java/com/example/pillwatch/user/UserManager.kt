package com.example.pillwatch.user

import com.example.pillwatch.storage.Storage
import javax.inject.Inject

class UserManager @Inject constructor(
    private val storage: Storage,
    private val userComponentFactory: UserComponent.Factory
) {

    var userComponent: UserComponent? = null
        private set

    val username: String
        get() = storage.getString("username")

    val email: String
        get() = storage.getString("email")

    val id: Long
        get() = storage.getLong("id");

    init {
        checkLogin()
    }

    private fun checkLogin() {
        if (id != -1L && email != "") {
            loginUser(id, email, username)
        }
    }

    fun isUserLoggedIn(): Boolean {
        checkLogin()
        return userComponent != null
    }

    fun loginUser(id: Long, email: String, username: String?): Boolean {
        storage.setLong("id", id)
        storage.setString("email", email)
        storage.setString("username", username ?: "")

        userJustLoggedIn()
        return true
    }

    fun setUsername(username: String) {
        storage.setString("username", username)
    }

    fun logout() {
        storage.setLong("id", -1L)
        storage.setString("email", "")
        storage.setString("username", "")
        userComponent = null
    }

    private fun userJustLoggedIn() {
        userComponent = userComponentFactory.create()
    }
}