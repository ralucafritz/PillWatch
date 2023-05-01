package com.example.pillwatch.user

import javax.inject.Inject

class UserDataRepository @Inject constructor(private val userManager: UserManager) {
    val username: String
        get() = userManager.username
}