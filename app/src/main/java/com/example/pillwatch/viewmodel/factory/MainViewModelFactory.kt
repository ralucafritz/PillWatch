package com.example.pillwatch.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.data.datasource.local.MedsDao
import com.example.pillwatch.data.datasource.local.MetadataDao
import com.example.pillwatch.data.datasource.local.UserDao
import com.example.pillwatch.viewmodel.MainViewModel

class MainViewModelFactory(
    private val medsDao: MedsDao,
    private val metadataDao: MetadataDao,
    private val userDao: UserDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(medsDao, metadataDao, userDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}