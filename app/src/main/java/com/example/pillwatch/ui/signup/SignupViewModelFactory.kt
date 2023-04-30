package com.example.pillwatch.ui.signup

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.data.source.local.UserDao

class SignupViewModelFactory(
    private val userDao: UserDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(userDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}