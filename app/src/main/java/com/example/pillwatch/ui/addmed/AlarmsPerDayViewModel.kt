package com.example.pillwatch.ui.addmed

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


class AlarmsPerDayViewModel @Inject constructor(private val alarmRepository: AlarmRepository): ViewModel() {

    var everyXHours: Int = 12

    suspend fun generateAlarms(medId: Long, alarmTiming: AlarmTiming) : MutableList<AlarmEntity> {
        val frequency = when (alarmTiming) {
            AlarmTiming.EVERY_X_HOURS -> {
                everyXHours
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
                add(Calendar.MILLISECOND, intervalMillis * i)
            }
            val alarm = AlarmEntity(0L, medId, calendar.timeInMillis, true)
            alarmList.add(alarm)
            withContext(Dispatchers.IO) {
                alarmRepository.insert(alarm)
            }
        }
        return alarmList
    }

    suspend fun updateAlarm(alarm: AlarmEntity) {
        withContext(Dispatchers.IO) {
            alarmRepository.updateAlarm(alarm)
        }
    }


}