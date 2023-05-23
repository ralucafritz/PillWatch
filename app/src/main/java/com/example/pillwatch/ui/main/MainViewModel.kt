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
import com.example.pillwatch.utils.Role
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

    private val _showNotification = MutableLiveData<Boolean>()
    val showNotification: LiveData<Boolean>
        get() = _showNotification

    private val _currentFragmentId = MutableLiveData<Int>()
    val currentFragmentId: LiveData<Int>
        get() = _currentFragmentId

    private val _previousFragmentId = MutableLiveData<Int>()
    val previousFragmentId: LiveData<Int>
        get() = _previousFragmentId

    private val _userRole = MutableLiveData(Role.USER)
    val userRole: LiveData<Role?>
        get() = _userRole

    private val _refreshUI = MutableLiveData<Boolean>()
    val refreshUI: LiveData<Boolean>
        get() = _refreshUI

    fun setCurrentFragmentId(id: Int) {
        _currentFragmentId.value = id
    }

    fun setPreviousFragment(id: Int) {
        _previousFragmentId.value = id
    }

    fun logout() {
        viewModelScope.launch {
            userManager.logout()
            delay(2000)
        }
    }

    fun showToast() {
        viewModelScope.launch {
            delay(2000)
            _showNotification.value = true
        }
    }

    fun getUserRole(userId: String) {
        viewModelScope.launch {
            _userRole.value = withContext(Dispatchers.IO) {
                userRepository.getRoleById(userId)
            }
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

    fun getDataFromCloud() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Load userMeds for the userId
                val userMedsList = userMedsRepository.getUserMedsFromCloudByUserId(userManager.id)
                if (userMedsList.isEmpty()) {
                    return@withContext
                }
                userMedsList.forEach { userMed ->
                    userMed?.let {
                        // Check if userMeds exists in the db
                        val userMedsExists = userMedsRepository.getMedById(userMed.id)
                        if (userMedsExists == null) {
                            userMedsRepository.insert(userMed)
                        }
                        // Load alarms for the userMedsId
                        val alarmsList = alarmRepository.getAlarmsFromCloudByMedId(userMed.id)
                        alarmsList.forEach { alarm ->
                            alarm?.let {
                                // Check if alarm exists in the db
                                val alarmExists = alarmRepository.getAlarmById(alarm.id)
                                if (alarmExists == null) {
                                    alarmRepository.insert(alarm)
                                }
                            }
                        }
                        // Load medsLog for the userMedsId
                        val medsLogList =
                            medsLogRepository.getMedsLogsFromCloudByMedId(userMed.id)
                        medsLogList.forEach { medsLog ->
                            medsLog?.let { medsLogEntity ->
                                // Check if medsLog exists in the db
                                val medsLogExists = medsLogRepository.getMedsLogById(medsLogEntity.id)
                                if (medsLogExists == null) {
                                    medsLogRepository.insert(medsLogEntity)
                                }
                            }
                        }
                    }
                }
                _refreshUI.postValue(true)
            }
        }
        userManager.justLoggedInStatus = false
    }
}