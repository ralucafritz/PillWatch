package com.example.pillwatch.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.data.source.local.AlarmDao
import com.example.pillwatch.data.source.local.MedsDao
import com.example.pillwatch.data.source.local.MedsLogDao
import com.example.pillwatch.data.source.local.MetadataDao
import com.example.pillwatch.data.source.local.UserDao
import com.example.pillwatch.data.source.local.UserMedsDao

class MainViewModelFactory(
    private val medsDao: MedsDao,
    private val metadataDao: MetadataDao,
    private val userDao: UserDao,
    private val userMedsDao: UserMedsDao,
    private val medsLogDao: MedsLogDao,
    private val alarmDao: AlarmDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(medsDao, metadataDao, userDao, userMedsDao, medsLogDao, alarmDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}