package com.example.pillwatch.alarms

import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.utils.AlarmTiming
import java.util.Calendar

class AlarmGenerator {

    fun generateAlarms(
        alarmTiming: AlarmTiming,
        medId: Long,
        everyXHours: Int,
        startHourInMillis: Long,
        regenCheck: Boolean = false
    ): List<AlarmEntity> {
        val frequency = when (alarmTiming) {
            AlarmTiming.EVERY_X_HOURS -> {
                24 / everyXHours
            }

            AlarmTiming.ONCE_A_DAY -> 1
            AlarmTiming.TWICE_A_DAY -> 2
            AlarmTiming.THREE_TIMES -> 3
            AlarmTiming.FOUR_TIMES -> 4
            AlarmTiming.NO_REMINDERS -> 0
        }

        val alarmList = mutableListOf<AlarmEntity>()
        val intervalMillis = (24 * 60 * 60 * 1000) / frequency

        var isRegen = regenCheck

        for (i in 0 until frequency) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = startHourInMillis
                add(Calendar.MILLISECOND, (intervalMillis * i))
            }
            if (!isRegen) {
                val alarm = AlarmEntity(0L, medId, calendar.timeInMillis, alarmTiming, true)
                alarmList.add(alarm)
            } else {
                isRegen = false
            }
        }
        return alarmList
    }
}