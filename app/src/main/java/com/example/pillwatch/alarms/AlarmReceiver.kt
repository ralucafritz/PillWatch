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
import androidx.core.content.ContextCompat
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.UserMedsRepository
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
    lateinit var alarmHandler: AlarmHandler

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as PillWatchApplication).appComponent.inject(this)
        val action = intent.action
        val alarmId = intent.getIntExtra("ALARM_ID", -1)

        if (alarmId == -1) {
            Timber.e("Invalid alarm ID")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val alarm = alarmRepository.getAlarmById(alarmId!!.toLong())
            if (alarm != null) {
                when (action) {
                    "OK_ACTION" -> handleOkAction(alarm, context, intent)
                    "POSTPONE_ACTION" -> handlePostponeAction(alarm, context, intent)
                    "MISSED_CHECK_ACTION" -> handleMissedAction(alarm)
                    else -> handleAlarmTriggered(context, alarm)
                }
            }
        }
    }

    private fun handleOkAction(alarm: AlarmEntity,  context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
        if (notificationId != -1) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(notificationId)
        }
        val stopAlarmIntent = Intent(context, AlarmService::class.java).apply {
            action = "STOP_ALARM"
        }
        context.startService(stopAlarmIntent)
        alarmHandler.createMedsLog(alarm, TakenStatus.TAKEN)
    }

    private fun handlePostponeAction(alarm: AlarmEntity,  context: Context,intent: Intent) {
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
        if (notificationId != -1) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(notificationId)
        }
        val stopAlarmIntent = Intent(context, AlarmService::class.java).apply {
            action = "STOP_ALARM"
        }
        context.startService(stopAlarmIntent)
        alarmHandler.postponeAlarm(alarm)
    }

    private fun handleMissedAction(alarm: AlarmEntity) {
        alarmHandler.createMedsLog(alarm, TakenStatus.MISSED)
    }

    private fun handleAlarmTriggered(context: Context, alarm: AlarmEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            val med = userMedsRepository.getMedById(alarm.medId)
            Timber.tag("ALARM RECEIVER").d("ok intent")
            val okIntent = Intent(context, AlarmReceiver::class.java).apply {
                action = "OK_ACTION"
                putExtra("ALARM_ID", alarm.id.toInt())
                putExtra("NOTIFICATION_ID", alarm.id.toInt())
            }
            val okPendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id.toInt(),
                okIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            Timber.tag("ALARM RECEIVER").d("postpone intent")
            val postponeIntent = Intent(context, AlarmReceiver::class.java).apply {
                action = "POSTPONE_ACTION"
                putExtra("ALARM_ID", alarm.id.toInt())
                putExtra("NOTIFICATION_ID", alarm.id.toInt())
            }
            val postponePendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id.toInt(),
                postponeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            Timber.tag("ALARM RECEIVER").d("notif builder")
            val notificationBuilder =
                NotificationCompat.Builder(context, "pill_watch_channel")
                    .setSmallIcon(R.drawable.ic_logo_pills_light)
                    .setContentTitle("Pill Reminder")
                    .setContentText("Time to take your medication: ${med.tradeName}")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .addAction(0, "OK", okPendingIntent)
                    .addAction(0, "Postpone", postponePendingIntent)
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
            notificationManager.notify(alarm.id.toInt(), notificationBuilder)
            Timber.tag("ALARM RECEIVER").d("alarm sound")

            // Play default alarm sound and vibrate for 30 seconds
            val startAlarmIntent = Intent(context, AlarmService::class.java).apply {
                action = "START_ALARM"
            }
            ContextCompat.startForegroundService(context, startAlarmIntent)

            // Schedule a missed check alarm in 1 hour
            val missedAlarmTime =
                System.currentTimeMillis() + (60 * 60 * 1000) // 1 hour from now
            alarmHandler.missedAlarm(alarm.id.toInt(), missedAlarmTime)

            // Schedule the next set of alarms
            alarmHandler.scheduleNextAlarms(med.id)
        }
    }

}

