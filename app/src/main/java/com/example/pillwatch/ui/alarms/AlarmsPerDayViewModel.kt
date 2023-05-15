package com.example.pillwatch.ui.alarms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.alarms.AlarmGenerator
import com.example.pillwatch.alarms.AlarmHandler
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.utils.AlarmTiming
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

/**
 * The ViewModel for managing alarms in the UI.
 *
 * @param alarmRepository The repository for accessing and modifying alarm data.
 * @param alarmGenerator The generator for creating alarm entities.
 * @param alarmHandler The handler for scheduling and handling alarms.
 */
class AlarmsPerDayViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmGenerator: AlarmGenerator,
    private val alarmHandler: AlarmHandler
) :
    ViewModel() {
    // MutableLiveData to hold the value of "everyXHours"
    var everyXHours = MutableLiveData<Int>()

    // Array of values to choose from for "everyXHours"
    val seekBarValues = arrayOf(1, 2, 3, 4, 6, 8, 12)

    // Variable to hold the ID of the medication
    var medId: Long = 0L

    // Variable to store the selected alarm timing option
    var alarmTiming: AlarmTiming = AlarmTiming.NO_REMINDERS

    // MutableLiveData to hold the start hour of alarms
    val startHour: MutableLiveData<Calendar> = MutableLiveData(Calendar.getInstance().apply {
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    })

    // Property to get the start hour in milliseconds
    private val startHourInMillis: Long?
        get() = startHour.value?.timeInMillis

    // MutableLiveData to hold the list of alarms
    private val _alarmsList = MutableLiveData<List<AlarmEntity>>(listOf())
    val alarmsList: LiveData<List<AlarmEntity>>
        get() = _alarmsList

    /**
     * Updates the start hour for generating alarms.
     *
     * @param hourOfDay The selected hour of the day.
     * @param minute The selected minute.
     */
    fun updateStartHour(selectedTime: Calendar) {
        this.startHour.value = selectedTime
        // Generate new alarms when the start hour is updated
        viewModelScope.launch {
            generateAlarms()
        }
    }

    /**
     * Coroutine function to generate alarms based on the selected options.
     */
    suspend fun generateAlarms() {
        val alarmsList = alarmGenerator.generateAlarms(
            alarmTiming,
            medId,
            everyXHours.value ?: 1,
            startHourInMillis!!
        )

        _alarmsList.value = alarmsList
    }

    /**
     * Updates an existing alarm.
     *
     * @param alarm The alarm entity to be updated.
     */
     fun updateAlarm(alarm: AlarmEntity) {
            sortAlarms(alarm)
    }

    /**
     * Sorts the list of alarms after an update and updates the repository.
     *
     * @param updatedAlarm The updated alarm entity.
     */
    private fun sortAlarms(updatedAlarm: AlarmEntity) {
            _alarmsList.value?.let {
                Timber.d("alarms: ${_alarmsList.value.toString()}")
                val updatedList = it.map { alarm ->
                    if (alarm.id == updatedAlarm.id) {
                        updatedAlarm
                    } else {
                        alarm
                    }
                }.sortedBy { alarm -> alarm.timeInMillis }
                // Update the list of alarms locally
                _alarmsList.value = updatedList
            }
    }

    /**
     * Schedules enabled alarms for medication and save to db.
     */
    fun scheduleAlarms() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                alarmRepository.clearForMedId(medId)
                alarmRepository.insertAll(_alarmsList.value!!)
            }
            val enabledAlarms = _alarmsList.value!!.filter { it.isEnabled }
            enabledAlarms.forEach { alarm ->
                alarmHandler.scheduleAlarm(alarm.id.toInt(), alarm.timeInMillis)
            }
        }
    }
}
