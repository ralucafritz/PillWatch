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
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Class responsible for handling alarms, including scheduling, canceling, and rescheduling.
 * Class also logs the alarms results.
 *
 * @param context The context of the application.
 * @param alarmGenerator The generator for creating alarm entities.
 * @param alarmRepository The repository for accessing and modifying alarm data.
 * @param medsLogRepository The repository for accessing and modifying medication log data.
 */
class AlarmHandler @Inject constructor(
    private val context: Context,
    private val alarmGenerator: AlarmGenerator,
    private val alarmRepository: AlarmRepository,
    private val medsLogRepository: MedsLogRepository
) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Creates a medication log entry for the specified alarm and status.
     *
     * @param medId The medication for which the log entry is created.
     * @param status The status of the medication (taken, postponed, missed).
     */
    fun createMedsLog(medId: String, status: TakenStatus) {
        CoroutineScope(Dispatchers.IO).launch {
            val timestamp = System.currentTimeMillis()
            val medsLog = MedsLogEntity(UUID.randomUUID().toString(), medId = medId, status = status, timestamp = timestamp)
            medsLogRepository.insert(medsLog)
        }
    }

    /**
     * Reschedules the specified alarm.
     *
     * @param alarm The alarm to be rescheduled.
     */
    fun rescheduleAlarm(alarm: AlarmEntity) {
        cancelAlarm(alarm.id)
        if (alarm.isEnabled) {
            scheduleAlarm(alarm.id, alarm.timeInMillis)
        }
    }

    /**
     * Schedules an alarm with the specified ID and time.
     *
     * @param alarmId The ID of the alarm.
     * @param alarmTime The time at which the alarm should be triggered.
     */
    fun scheduleAlarm(alarmId: String, alarmTime: Long, postponedTimes: Int = 0) {
        if (alarmTime > System.currentTimeMillis()) {
            val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("ALARM_ID", alarmId)
                if(postponedTimes != 0) {
                    putExtra("POSTPONED_TIMES", postponedTimes)
                }
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId.hashCode(),
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                pendingIntent
            )
            Timber.tag("ALARM HANDLER").d("Scheduled alarm $alarmId")
        }
    }

    /**
     * Cancels the alarm with the specified ID.
     *
     * @param alarmId The ID of the alarm to be canceled.
     */
    fun cancelAlarm(alarmId: String) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.hashCode(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        Timber.tag("ALARM HANDLER").d("Canceled scheduled alarm $alarmId ")
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Postpones the specified alarm by 5 minutes.
     *
     * @param alarm The alarm to be postponed.
     */
    fun postponeAlarm(alarm: AlarmEntity, postponedTimes: Int) {
        createMedsLog(alarm.medId, TakenStatus.POSTPONED)
        val postponeTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)
        scheduleAlarm(alarm.id, postponeTime, postponedTimes)
        Timber.tag("ALARM HANDLER").d("Postponed scheduled alarm ${alarm.id} ")
    }

    /**
     * Triggers a missed alarm with the specified ID and time.
     *
     * @param alarmId The ID of the missed alarm.
     * @param alarmTime The original time of the missed alarm.
     */
    fun missedAlarm(alarmId: String, originalTime: Long, alarmTime: Long) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "MISSED_CHECK_ACTION"
            putExtra("ALARM_ID", alarmId)
            putExtra("ORIGINAL_ALARM_TIME", originalTime)
            putExtra("NEW_ALARM_TIME", alarmTime)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.hashCode(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
        Timber.tag("ALARM HANDLER").d("Scheduled missed check for alarm $alarmId ")

    }

    /**
     * Checks the medication logs for the specified medication ID within the timeframe
     * from the original alarm time to the missed check time. If no log entry is found,
     * creates a medication log entry with the status as MISSED.
     *
     * @param medId The ID of the medication.
     * @param originalTime The time of the original alarm.
     * @param missedCheckTime The time of the missed check.
     */

    fun checkLogsForTakenOrPostponed(medId: String, originalTime: Long, missedCheckTime: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val logEntries =
                medsLogRepository.getLogInTimeframeByMedId(medId, originalTime, missedCheckTime)
            if (logEntries.isEmpty()) {
                createMedsLog(medId, TakenStatus.MISSED)
            }
        }
    }

    /**
     * Reschedules and regenerates alarms for the specified medication ID.
     *
     * @param medId The ID of the medication for which to reschedule alarms.
     */
    fun scheduleNextAlarms(medId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Timber.tag("ALARM HANDLER").d("Reschedule and regen alarms for med no. $medId")
            val currentTime = System.currentTimeMillis()
            val lastAlarm = alarmRepository.getLastAlarmByMedId(medId)
            lastAlarm?.let {
                if (currentTime > lastAlarm.timeInMillis) {
                    val newAlarmList = generateAlarms(lastAlarm, currentTime)

                    alarmRepository.clearForMedId(medId).await()
                    alarmRepository.insertAll(newAlarmList)

                    newAlarmList.forEach { alarm ->
                        scheduleAlarm(alarm.id, alarm.timeInMillis)
                    }
                }
            }
        }
    }

    private fun generateAlarms(lastAlarm: AlarmEntity, currentTime: Long): List<AlarmEntity> {
        val newAlarmList = alarmGenerator.generateAlarms(
            lastAlarm.alarmTiming,
            lastAlarm.medId,
            lastAlarm.everyXHours ?: 1,
            lastAlarm.timeInMillis,
            true
        )

        val newGeneratedLastAlarm = newAlarmList.maxByOrNull { it.timeInMillis }
        return if (newGeneratedLastAlarm!!.timeInMillis < currentTime) {
            generateAlarms(newGeneratedLastAlarm, currentTime)
        } else {
            newAlarmList
        }
    }
}
