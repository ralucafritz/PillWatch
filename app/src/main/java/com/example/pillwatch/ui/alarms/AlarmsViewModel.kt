package com.example.pillwatch.ui.alarms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.alarms.AlarmGenerator
import com.example.pillwatch.alarms.AlarmReceiverCallback
import com.example.pillwatch.alarms.AlarmScheduler
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.utils.AlarmTiming
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

class AlarmsViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmGenerator: AlarmGenerator,
    private val alarmScheduler: AlarmScheduler
) :
    ViewModel() {

    var everyXHours = MutableLiveData<Int>()

    val values = arrayOf(1, 2, 3, 4, 6, 8, 12)

    var medId: Long = 0L

    var alarmTiming: AlarmTiming = AlarmTiming.NO_REMINDERS

    val startHour: MutableLiveData<Calendar> = MutableLiveData(Calendar.getInstance().apply {
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    })

    private val startHourInMillis: Long?
        get() = startHour.value?.timeInMillis

    private val _alarmsList = MutableLiveData<MutableList<AlarmEntity>>(mutableListOf())
    val alarmsList: LiveData<MutableList<AlarmEntity>>
        get() = _alarmsList

    fun updateStartHour(hourOfDay: Int, minute: Int) {
        val startHour = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        this.startHour.value = startHour

        viewModelScope.launch {
            generateAlarms()
        }
    }

    suspend fun generateAlarms() {
        val alarmsList = alarmGenerator.generateAlarms(
            alarmTiming,
            medId,
            everyXHours.value!!,
            startHourInMillis!!
        )

        _alarmsList.value = withContext(Dispatchers.IO) {
            _alarmsList.value!!.forEach { alarm ->
                alarmScheduler.cancelAlarm(alarm)
            }
            alarmRepository.clearForMedId(medId)
            alarmRepository.insertAll(alarmsList)
            alarmRepository.getAlarmsByMedId(medId).toMutableList()
        }!!

        _alarmsList.value!!.forEach { alarm ->
            alarmScheduler.scheduleAlarm(alarm)
        }
    }

    suspend fun updateAlarm(alarm: AlarmEntity) {
        withContext(Dispatchers.IO) {
            val updatedAlarm = alarm.copy()
            alarmRepository.updateAlarm(updatedAlarm)
            sortAlarms(updatedAlarm)
        }
        if (!alarm.isEnabled) {
            alarmScheduler.cancelAlarm(alarm)
        } else {
            alarmScheduler.scheduleAlarm(alarm)
        }
    }

    fun getAlarmById(alarmId: Long, callback: AlarmReceiverCallback) {
        viewModelScope.launch {
            val alarm = withContext(Dispatchers.IO) {
                alarmRepository.getAlarmById(alarmId)
            }
            alarm?.let {
                callback.onAlarmFetched(it)
            }
        }
    }

    private fun sortAlarms(updatedAlarm: AlarmEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                alarmRepository.clearForMedId(medId)
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
}
