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

/**
 * ViewModel for managing the medication page.
 *
 * @param userMedsRepository The repository for accessing and modifying user medication data.
 * @param alarmRepository The repository for accessing and modifying alarm data.
 * @param medsLogRepository The repository for accessing and modifying medication log data.
 * @param alarmHandler The handler for scheduling and handling alarms.
 */
class MedPageViewModel @Inject constructor(
    private val userMedsRepository: UserMedsRepository,
    private val alarmRepository: AlarmRepository,
    private val medsLogRepository: MedsLogRepository,
    private val alarmHandler: AlarmHandler
): ViewModel() {

    private val _medEntity = MutableLiveData<UserMedsEntity?>()
    val medEntity: LiveData<UserMedsEntity?>
        get() = _medEntity

    private val _alarmsList = MutableLiveData<List<AlarmEntity>?>()
    val alarmsList: LiveData<List<AlarmEntity>?>
        get() = _alarmsList
    /**
     * Retrieves the medication entity from the repository based on the specified ID.
     *
     * @param id The ID of the medication.
     */
    fun getMedEntity(id: Long) {
        viewModelScope.launch {
            _medEntity.value = withContext(Dispatchers.IO) {
                userMedsRepository.getMedById(id)
            }
        }
    }
    /**
     * Retrieves the list of alarms associated with the medication.
     */
    fun getAlarms() {
        viewModelScope.launch {
            _alarmsList.value = withContext(Dispatchers.IO) {
                alarmRepository.getAlarmsByMedId(medEntity.value!!.id).toMutableList()
            }
        }
    }
    /**
     * Updates an existing alarm and reschedules it.
     *
     * @param alarm The alarm entity to be updated.
     */
    suspend fun updateAlarm(alarm: AlarmEntity) {
        withContext(Dispatchers.IO) {
            val updatedAlarm = alarm.copy()
            alarmRepository.updateAlarm(updatedAlarm)
            alarmHandler.rescheduleAlarm(updatedAlarm)
            sortAlarms(updatedAlarm)
        }
    }
    /**
     * Sorts the list of alarms after an update and updates the repository.
     *
     * @param updatedAlarm The updated alarm entity.
     */
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
                _alarmsList.value = updatedList
            }
            withContext(Dispatchers.IO) {
                alarmRepository.insertAll(_alarmsList.value!!.toList())
            }
        }
    }
    /**
     * Deletes the medication and cancels associated alarms.
     */
    fun deleteMed() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if(_alarmsList.value != null)
                    _alarmsList.value!!.forEach {
                        // Cancel all associated alarms
                        alarmHandler.cancelAlarm(it.id.toInt())
                    }
                // Delete the medication from the repository
                userMedsRepository.deleteById(_medEntity.value!!.id)
            }
        }
    }
}