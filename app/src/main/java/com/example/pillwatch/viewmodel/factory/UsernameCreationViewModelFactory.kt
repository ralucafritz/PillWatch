package com.example.pillwatch.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.data.datasource.local.UserDao
import com.example.pillwatch.viewmodel.UsernameCreationViewModel

class UsernameCreationViewModelFactory(
    private val userDao: UserDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsernameCreationViewModel::class.java)) {
            return UsernameCreationViewModel(userDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}