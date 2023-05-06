package com.example.pillwatch.alarms

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pillwatch.R
import com.example.pillwatch.data.model.AlarmEntity
import timber.log.Timber

class AlarmScheduler(private val context: Context) {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val EXTRA_ALARM_ID = "com.example.pillwatch.EXTRA_ALARM_ID"
        const val ACTION_TAKEN = "com.example.pillwatch.ACTION_TAKEN"
        const val ACTION_POSTPONED = "com.example.pillwatch.ACTION_POSTPONED"
        const val ACTION_MISSED = "com.example.pillwatch.ACTION_MISSED"
    }

    /**
     *  schedule an alarm using AlarmManager
     * */
    fun scheduleAlarm(alarm: AlarmEntity) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarm.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        Timber.d("SCHEDULED ALARM ${alarm.id}")
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.timeInMillis,
            pendingIntent
        )
    }

    /**
     *  cancel an alarm using AlarmManager
     * */
    fun cancelAlarm(alarm: AlarmEntity) {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarm.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        Timber.d("CANCELED SCHEDULED ALARM ${alarm.id} ")
        alarmManager.cancel(pendingIntent)
    }

    /**
     *  show a notification using NotificationCompat
     * */
    fun showNotification(context: Context, alarm: AlarmEntity) {
        val notification = NotificationCompat.Builder(context, "pill_watch_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("PillWatch Reminder")
            .setContentText("It's time to take your medication.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
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
            return
        }
        notificationManager.notify(alarm.id.toInt(), notification)
    }
}