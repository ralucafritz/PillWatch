package com.example.pillwatch.alarms

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.storage.Storage
import com.example.pillwatch.utils.TakenStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var userMedsRepository: UserMedsRepository

    @Inject
    lateinit var storage: Storage

    @Inject
    lateinit var alarmHandler: AlarmHandler

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as PillWatchApplication).appComponent.inject(this)
        val action = intent.action
        val alarmId = intent.getStringExtra("ALARM_ID")
        val postponedTimes = intent.getIntExtra("POSTPONED_TIMES", -1)
        if (alarmId.isNullOrEmpty()) {
            Timber.e("Invalid alarm ID")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val alarm = alarmRepository.getAlarmById(alarmId)
            if (alarm != null) {
                when (action) {
                    "OK_ACTION" -> handleOkAction(alarm, context, intent)
                    "POSTPONE_ACTION" -> handlePostponeAction(
                        alarm,
                        context,
                        intent,
                        postponedTimes
                    )

                    "MISSED_CHECK_ACTION" -> handleMissedAction(alarm, intent)
                    else -> handleAlarmTriggered(context, alarm, postponedTimes)
                }
            }
        }
    }

    private fun handleOkAction(alarm: AlarmEntity, context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
        Timber.tag("ALARM RECEIVER").d(" notification with ID: $notificationId")
        if (notificationId != -1) {
            val notificationManager = NotificationManagerCompat.from(context)
            Timber.tag("ALARM RECEIVER").d("Canceling notification with ID: $notificationId")
            notificationManager.cancel(notificationId)
        }
        stopAlarmService(context)
        alarmHandler.createMedsLog(alarm.medId, TakenStatus.TAKEN)
    }

    private fun handlePostponeAction(
        alarm: AlarmEntity,
        context: Context,
        intent: Intent,
        postponedTimes: Int
    ) {
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
        if (notificationId != -1) {
            val notificationManager = NotificationManagerCompat.from(context)
            Timber.d("Canceling notification with ID: $notificationId")
            notificationManager.cancel(notificationId)
        }
        Timber.d("Postponed times: ${postponedTimes + 1}")
        stopAlarmService(context)
        alarmHandler.postponeAlarm(alarm, postponedTimes + 1)
    }

    private fun handleMissedAction(alarm: AlarmEntity, intent: Intent) {
        val originalAlarmTime = intent.getLongExtra("ORIGINAL_ALARM_TIME", -1L)
        val newAlarmTime = intent.getLongExtra("NEW_ALARM_TIME", -1L)
        CoroutineScope(Dispatchers.IO).launch {
            alarmHandler.checkLogsForTakenOrPostponed(alarm.medId, originalAlarmTime, newAlarmTime)
        }
        Timber.tag("ALARM RECEIVER").d("Missed scheduled alarm")
    }

    private fun handleAlarmTriggered(context: Context, alarm: AlarmEntity, postponedTimes: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val med = userMedsRepository.getMedById(alarm.medId)
            Timber.tag("ALARM RECEIVER").d("ok intent")
            val okIntent = Intent(context, AlarmReceiver::class.java).apply {
                action = "OK_ACTION"
                putExtra("ALARM_ID", alarm.id)
                putExtra("NOTIFICATION_ID", alarm.id.hashCode())
                putExtra("POSTPONED_TIMES", postponedTimes)
            }
            val okPendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id.hashCode(),
                okIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            Timber.tag("ALARM RECEIVER").d("postpone intent")
            val postponeIntent = Intent(context, AlarmReceiver::class.java).apply {
                action = "POSTPONE_ACTION"
                putExtra("ALARM_ID", alarm.id)
                putExtra("NOTIFICATION_ID", alarm.id.hashCode())
                putExtra("POSTPONED_TIMES", postponedTimes)
            }
            val postponePendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id.hashCode(),
                postponeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            Timber.tag("ALARM RECEIVER").d("notif builder")
            val alarmMessage = storage.getAlarmNotificationMessage()
            val name = med.tradeName
            val notificationBuilder =
                NotificationCompat.Builder(context, "pill_watch_channel")
                    .setSmallIcon(R.drawable.ic_logo_pills_light)
                    .setContentTitle(context.resources.getString(R.string.notification_title))
                    .setContentText("$alarmMessage $name")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .addAction(0, context.resources.getString(R.string.notification_ok), okPendingIntent)
                    .addAction(0, context.resources.getString(R.string.notification_postpone), postponePendingIntent)
                    .setAutoCancel(true)
                    .build()
            Timber.tag("ALARM RECEIVER").d("notif manager")
            val notificationManager = NotificationManagerCompat.from(context)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@launch
            }
            notificationManager.notify(alarm.id.hashCode(), notificationBuilder)
            Timber.tag("ALARM RECEIVER").d("alarm sound")

            // Play default alarm sound and vibrate for 30 seconds
            val startAlarmIntent = Intent(context, AlarmService::class.java).apply {
                action = "START_ALARM"
                putExtra("POSTPONED_TIMES", postponedTimes)
            }

            context.startService(startAlarmIntent)

            // Schedule a missed check alarm in 1 hour
            val currentTime = System.currentTimeMillis()
            val missedAlarmTime =
                System.currentTimeMillis() + (60 * 60 * 1000) // 1 hour from now
            alarmHandler.missedAlarm(alarm.id, currentTime, missedAlarmTime)

            // Schedule the next set of alarms
            alarmHandler.scheduleNextAlarms(med.id)
        }
    }

    private fun stopAlarmService(context: Context) {
        val stopAlarmIntent = Intent(context, AlarmService::class.java).apply {
            action = "STOP_ALARM"
        }
        context.startService(stopAlarmIntent)
    }
}

