package com.example.pillwatch.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.data.datasource.local.AlarmDao
import com.example.pillwatch.data.datasource.local.MedsDao
import com.example.pillwatch.data.datasource.local.MedsLogDao
import com.example.pillwatch.data.datasource.local.MetadataDao
import com.example.pillwatch.data.datasource.local.UserDao
import com.example.pillwatch.data.datasource.local.UserMedsDao
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.MedsRepository
import com.example.pillwatch.data.repository.MedsLogRepository
import com.example.pillwatch.data.repository.MetadataRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.utils.extensions.ContextExtensions.setLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.setPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    medsDao: MedsDao,
    metadataDao: MetadataDao,
    userDao: UserDao,
    userMedsDao: UserMedsDao,
    medsLogDao: MedsLogDao,
    alarmDao: AlarmDao,
    application: Application)
    : AndroidViewModel(application) {

    private val medsRepository = MedsRepository(medsDao)
    private val metadataRepository = MetadataRepository(metadataDao)
    private val userRepository = UserRepository(userDao)
    private val userMedsRepository = UserMedsRepository(userMedsDao)
    private val medsLogRepository = MedsLogRepository(medsLogDao)
    private val alarmRepository = AlarmRepository(alarmDao)

    private val _currentFragmentId = MutableLiveData<Int>()
    val currentFragmentId: LiveData<Int>
        get() = _currentFragmentId

    private val context: Context by lazy { application.applicationContext }

    fun setCurrentFragmentId(id: Int) {
        _currentFragmentId.value = id
    }

    fun logout() {
        context.setLoggedInStatus(false)
        context.setPreference("email", "")
        context.setPreference("id", 0L)
        context.setPreference("username", "")
    }

    fun clear() {
        viewModelScope.launch{
//            cleanMedsData()
//            cleanMetadata()
//            cleanUsers()
            cleanUserMeds()
            cleanAlarms()
            cleanMedsLog()
        }
    }

    private suspend fun cleanMedsData() {
        withContext(Dispatchers.IO) {
            medsRepository.clear()
        }
    }


    private suspend fun cleanMetadata() {
        withContext(Dispatchers.IO) {
            metadataRepository.clear()
        }
    }


    private suspend fun cleanUsers() {
        withContext(Dispatchers.IO) {
            userRepository.clear()
        }
    }

    private suspend fun cleanUserMeds() {
        withContext(Dispatchers.IO) {
            userMedsRepository.clear()
        }
    }
    private suspend fun cleanAlarms() {
        withContext(Dispatchers.IO) {
            alarmRepository.clear()
        }
    }
    private suspend fun cleanMedsLog() {
        withContext(Dispatchers.IO) {
            medsLogRepository.clear()
        }
    }
}