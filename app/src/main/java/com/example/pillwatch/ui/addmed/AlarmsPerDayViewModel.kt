package com.example.pillwatch.ui.addmed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.utils.AlarmTiming
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

class AlarmsPerDayViewModel @Inject constructor(private val alarmRepository: AlarmRepository) :
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
            regenerateAlarms()
        }
    }

    private suspend fun regenerateAlarms() {
        withContext(Dispatchers.IO) {
            val x = medId
            alarmRepository.clearForMedId(x)
        }
        generateAlarms()
    }

    suspend fun generateAlarms() {
        val frequency = when (alarmTiming) {
            AlarmTiming.EVERY_X_HOURS -> {
                24 / everyXHours.value!!
            }

            AlarmTiming.ONCE_A_DAY -> 1
            AlarmTiming.TWICE_A_DAY -> 2
            AlarmTiming.THREE_TIMES -> 3
            AlarmTiming.FOUR_TIMES -> 4
            AlarmTiming.FIVE_TIMES -> 5
            AlarmTiming.NO_REMINDERS -> 0
        }

        val alarmList = mutableListOf<AlarmEntity>()
        val intervalMillis = (24 * 60 * 60 * 1000) / frequency

        for (i in 0 until frequency) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = startHourInMillis!!
                add(Calendar.MILLISECOND, (intervalMillis * i).toInt())
            }
            val alarm = AlarmEntity(0L, medId, calendar.timeInMillis, true)
            alarmList.add(alarm)
        }
        _alarmsList.value = withContext(Dispatchers.IO) {
            alarmRepository.clearForMedId(medId)
            alarmRepository.insertAll(alarmList.toList())
            alarmRepository.getAlarmsByMedId(medId).toMutableList()
        }!!
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
                alarmRepository.clearForMedId(medId)
            }
            _alarmsList.value?.let {
                val updatedList = it.map { alarm ->
                    val x = alarm.id
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
