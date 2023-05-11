package com.example.pillwatch.alarms

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as PillWatchApplication).appComponent.inject(this)
        val action = intent.action
        val alarmId = intent.getLongExtra("ALARM_ID", -1L)

        if (alarmId == -1L) {
            Timber.e("Invalid alarm ID")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val alarm = alarmRepository.getAlarmById(alarmId)
            if (alarm != null) {
                when (action) {
                    Intent.ACTION_BOOT_COMPLETED -> handleBootCompleted()
                    "OK_ACTION" -> handleOkAction(alarm)
                    "POSTPONE_ACTION" -> handlePostponeAction(alarm)
                    "MISSED_CHECK_ACTION" -> handleMissedAction(alarm)
                    "ALARM_TRIGGERED" -> handleAlarmTriggered(context, alarm)
                    else -> Timber.e("Unknown action received")
                }
            }
        }
    }

    private fun handleBootCompleted() {
        alarmHandler.scheduleNextAlarms()
    }

    private fun handleOkAction(alarm: AlarmEntity) {
        stopAlarmAndVibration()
        alarmHandler.createMedsLog(alarm, TakenStatus.TAKEN)
    }

    private fun handlePostponeAction(alarm: AlarmEntity) {
        stopAlarmAndVibration()
        alarmHandler.postponeAlarm(alarm)
    }

    private fun handleMissedAction(alarm: AlarmEntity) {
        alarmHandler.createMedsLog(alarm, TakenStatus.MISSED)
    }

    private fun handleAlarmTriggered(context: Context, alarm: AlarmEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            val med = userMedsRepository.getMedById(alarm.medId)

            val okIntent = Intent(context, AlarmReceiver::class.java).apply {
                action = "OK_ACTION"
                putExtra("ALARM_ID", alarm.id)
            }
            val okPendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id.toInt(),
                okIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val postponeIntent = Intent(context, AlarmReceiver::class.java).apply {
                action = "POSTPONE_ACTION"
                putExtra("ALARM_ID", alarm.id)
            }
            val postponePendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id.toInt(),
                postponeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notificationBuilder =
                NotificationCompat.Builder(context, "pill_watch_channel")
                    .setSmallIcon(R.drawable.ic_logo_pills_light)
                    .setContentTitle("Pill Reminder")
                    .setContentText("Time to take your medication: ${med.tradeName}")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .addAction(0, "OK", okPendingIntent)
                    .addAction(0, "Postpone", postponePendingIntent)
                    .build()

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

            // Play default alarm sound for 30 seconds
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            mediaPlayer = MediaPlayer.create(context, alarmUri)
            mediaPlayer!!.start()
            Handler(Looper.getMainLooper()).postDelayed({
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
            }, 30000) // 30 seconds

            // Vibrate
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator!!.vibrate(
                VibrationEffect.createOneShot(
                    2000,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }

        // Schedule a missed check alarm in 1 hour
        val missedAlarmTime =
            System.currentTimeMillis() + (60 * 60 * 1000) // 1 hour from now
        alarmHandler.missedAlarm(alarm.id.toInt(), missedAlarmTime)

        // Schedule the next set of alarms
        alarmHandler.scheduleNextAlarms()
    }

    private fun stopAlarmAndVibration() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        vibrator?.cancel()
        vibrator = null
    }

}

