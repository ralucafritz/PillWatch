package com.example.pillwatch.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.data.datasource.local.UserDao
import com.example.pillwatch.viewmodel.LoginViewModel

class LoginViewModelFactory (
    private val userDao: UserDao,
    private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(userDao, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }