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

    val id: String
        get() = storage.getString("id")

    init {
        checkLogin()
    }

    private fun checkLogin() {
        if (id != "" && email != "") {
            loginUser(id, email, username)
        }
    }

    fun isUserLoggedIn(): Boolean {
        checkLogin()
        return userComponent != null
    }

    fun loginUser(id: String, email: String, username: String?): Boolean {
        storage.setString("id", id)
        storage.setString("email", email)
        storage.setString("username", username ?: "")

        userJustLoggedIn()
        return true
    }

    var theme: String
        get() = storage.getTheme()
        set(themeSetting: String) {
            storage.setTheme(themeSetting)
        }

    val messageIndex: Int
        get() = storage.getMessageIndex()

    var alarmMessage: String
        get() = storage.getAlarmNotificationMessage()
        set(message: String) {
            storage.setAlarmNotificationMessage(message)
        }

    fun setMessageIndex(index: Int) {
        storage.setMessageIndex(index)
    }

    fun setUsername(username: String) {
        storage.setString("username", username)
    }

    fun logout() {
        storage.setString("id", "")
        storage.setString("email", "")
        storage.setString("username", "")
        userComponent = null
    }

    private fun userJustLoggedIn() {
        userComponent = userComponentFactory.create()
    }
}