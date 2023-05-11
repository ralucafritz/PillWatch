package com.example.pillwatch.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.model.MedsLogEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.MedsLogRepository
import com.example.pillwatch.utils.TakenStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AlarmHandler @Inject constructor(
    private val context: Context,
    private val alarmGenerator: AlarmGenerator,
    private val alarmRepository: AlarmRepository,
    private val medsLogRepository: MedsLogRepository
) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun createMedsLog(alarm: AlarmEntity, status: TakenStatus) {
        CoroutineScope(Dispatchers.IO).launch {
            val medId = alarm.medId
            val timestamp = System.currentTimeMillis()
            val medsLog = MedsLogEntity(medId = medId, status = status, timestamp = timestamp)
            medsLogRepository.insert(medsLog)
        }
    }

    fun scheduleAlarm(alarmId: Int, alarmTime: Long) {
        Timber.tag("ALARM HANDLER").d(" schedule alarm")
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarmId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        Timber.tag("ALARM HANDLER").d("SCHEDULED ALARM $alarmId")
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pendingIntent
        )
    }

    /**
     *  cancel an alarm using AlarmManager
     * */
    fun cancelAlarm(alarmId: Int) {
        Timber.tag("ALARM HANDLER").d(" cancel alarm")
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        Timber.tag("ALARM HANDLER").d("CANCELED SCHEDULED ALARM $alarmId ")
        alarmManager.cancel(pendingIntent)
    }

    fun postponeAlarm(alarm: AlarmEntity) {
        createMedsLog(alarm, TakenStatus.POSTPONED)
        val postponeTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)
        scheduleAlarm(alarm.id.toInt(), postponeTime)
        Timber.tag("ALARM HANDLER").d("POSTPONE SCHEDULE ALARM ${alarm.id} ")
    }

    fun missedAlarm(alarmId: Int, alarmTime: Long) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "MISSED_CHECK_ACTION"
            putExtra("ALARM_ID", alarmId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
        Timber.tag("ALARM HANDLER").d("Missed SCHEDULE ALARM $alarmId ")

    }

    fun scheduleNextAlarms(medId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            Timber.tag("ALARM HANDLER").d("RESCHEDULE ALARMS FOR MED $medId")
            val currentTime = System.currentTimeMillis()
            val lastAlarm = alarmRepository.getLastAlarmByMedId(medId)
            if (currentTime > lastAlarm.timeInMillis) {
                val newAlarmList = alarmGenerator.generateAlarms(
                    lastAlarm.alarmTiming,
                    lastAlarm.medId,
                    lastAlarm.everyXHours ?: 1,
                    lastAlarm.timeInMillis,
                    true
                )
                alarmRepository.clearForMedId(medId)
                alarmRepository.insertAll(newAlarmList)

                newAlarmList.forEach { alarm ->
                    scheduleAlarm(alarm.id.toInt(), alarm.timeInMillis)
                }
            }
        }
    }
}
