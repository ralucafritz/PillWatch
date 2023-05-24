package com.example.pillwatch.ui.medication.medpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.alarms.AlarmHandler
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.model.MedsLogEntity
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.MedsLogRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _logs = MutableLiveData<List<MedsLogEntity>?>()
    val logs: LiveData<List<MedsLogEntity>?>
        get() = _logs

    private val _hasAlarms = MutableLiveData<Boolean>()
    val hasAlarms: LiveData<Boolean>
        get() = _hasAlarms

    /**
     * Retrieves the medication entity from the repository based on the specified ID.
     *
     * @param id The ID of the medication.
     */
    fun getMedEntity(id: String) {
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
            _hasAlarms.value = (alarmsList.value != null && alarmsList.value!!.isNotEmpty())
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
                alarmRepository.updateAlarm(updatedAlarm)
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
            if(updatedAlarm.isEnabled && !medEntity.value!!.isArchived) {
                alarmHandler.rescheduleAlarm(updatedAlarm)
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
                        alarmHandler.cancelAlarm(it.id)
                    }
                // Delete the medication from the repository
                userMedsRepository.deleteById(_medEntity.value!!.id)
            }
        }
    }

    fun archive(bool: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userMedsRepository.archiveMed(medEntity.value!!.id, bool)
                val updatedMedEntity = userMedsRepository.getMedById(medEntity.value!!.id)
                _medEntity.postValue(updatedMedEntity)
                if(bool) {
                    _alarmsList.value?.forEach {
                        alarmHandler.scheduleAlarm(it.id, it.timeInMillis)
                    }
                } else {
                    _alarmsList.value?.forEach {
                        alarmHandler.cancelAlarm(it.id)
                    }
                }
            }
        }
    }

    /**
     * Updates the medication name and concentration.
     *
     * @param newName The new name of the medication.
     * @param newConcentration The new concentration of the medication.
     */
    fun updateMedName(newName: String, newConcentration: String) {
        viewModelScope.launch {
            val medEntity = _medEntity.value
            if (medEntity != null) {
                val updatedMedEntity = medEntity.copy(tradeName = newName, concentration = newConcentration)
                withContext(Dispatchers.IO) {
                    userMedsRepository.updateMed(updatedMedEntity)
                }
                _medEntity.value = updatedMedEntity
            }
        }
    }

    /**
     * Retrieves the medication logs associated with the medication.
     */
    fun getLogs() {
        viewModelScope.launch {
            _logs.value = withContext(Dispatchers.IO) {
                medsLogRepository.getLogByMedId(_medEntity.value!!.id)
            }
        }
    }
}