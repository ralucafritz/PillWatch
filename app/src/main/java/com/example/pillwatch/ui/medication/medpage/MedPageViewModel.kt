package com.example.pillwatch.ui.medication.medpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.alarms.AlarmHandler
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.MedsLogRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MedPageViewModel @Inject constructor(
    private val userMedsRepository: UserMedsRepository,
    private val alarmRepository: AlarmRepository,
    private val medsLogRepository: MedsLogRepository,
    private val alarmHandler: AlarmHandler
): ViewModel() {

    private val _medEntity = MutableLiveData<UserMedsEntity?>()
    val medEntity: LiveData<UserMedsEntity?>
        get() = _medEntity

    private val _alarmsList = MutableLiveData<MutableList<AlarmEntity>?>()
    val alarmsList: LiveData<MutableList<AlarmEntity>?>
        get() = _alarmsList

    fun getMedEntity(id: Long) {
        viewModelScope.launch {
            _medEntity.value = withContext(Dispatchers.IO) {
                userMedsRepository.getMedById(id)
            }
        }
    }

    fun getAlarms() {
        viewModelScope.launch {
            _alarmsList.value = withContext(Dispatchers.IO) {
                alarmRepository.getAlarmsByMedId(medEntity.value!!.id).toMutableList()
            }
        }
    }

    suspend fun updateAlarm(alarm: AlarmEntity) {
        withContext(Dispatchers.IO) {
            val updatedAlarm = alarm.copy()
            alarmRepository.updateAlarm(updatedAlarm)
            sortAlarms(updatedAlarm)
        }
    }

    private fun sortAlarms(updatedAlarm: AlarmEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                alarmRepository.clearForMedId(medEntity.value!!.id)
            }
            _alarmsList.value?.let {
                val updatedList = it.map { alarm ->
                    if (alarm.id == updatedAlarm.id) {
                        updatedAlarm
                    } else {
                        alarm
                    }
                }.sortedBy { alarm -> alarm.timeInMillis }
                _alarmsList.value = updatedList.toMutableList()
            }
            withContext(Dispatchers.IO) {
                alarmRepository.insertAll(_alarmsList.value!!.toList())
            }
        }
    }

    fun deleteMed() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if(_alarmsList.value != null)
                    _alarmsList.value!!.forEach {
                        alarmHandler.cancelAlarm(it.id.toInt())
                    }
                userMedsRepository.deleteById(_medEntity.value!!.id)
            }
        }
    }
}