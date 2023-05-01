package com.example.pillwatch.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.MedsRepository
import com.example.pillwatch.data.repository.MedsLogRepository
import com.example.pillwatch.data.repository.MetadataRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.di.LoggedUserScope
import com.example.pillwatch.user.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@LoggedUserScope
class MainViewModel @Inject constructor(
    private val medsRepository: MedsRepository,
    private val metadataRepository: MetadataRepository,
    private val userRepository: UserRepository,
    private val userMedsRepository: UserMedsRepository,
    private val medsLogRepository: MedsLogRepository,
    private val alarmRepository: AlarmRepository,
    private val userManager: UserManager
) : ViewModel() {

    private val _currentFragmentId = MutableLiveData<Int>()
    val currentFragmentId: LiveData<Int>
        get() = _currentFragmentId

    fun setCurrentFragmentId(id: Int) {
        _currentFragmentId.value = id
    }

    fun logout() {
        viewModelScope.launch {
        userManager.logout()
            delay(2000)
        }
    }

    fun clear() {
        viewModelScope.launch {
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