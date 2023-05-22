package com.example.pillwatch

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pillwatch.alarms.AlarmSchedulerWorker
import com.example.pillwatch.di.AppComponent
import com.example.pillwatch.di.DaggerAppComponent
import java.util.concurrent.TimeUnit

class PillWatchApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel(this)
        scheduleAlarmSchedulerWorker(this)
    }

    private fun scheduleAlarmSchedulerWorker(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val alarmSchedulerWorkerRequest =
            PeriodicWorkRequestBuilder<AlarmSchedulerWorker>(1, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

        workManager.enqueueUniquePeriodicWork(
            "AlarmSchedulerWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            alarmSchedulerWorkerRequest
        )
    }

    private fun createNotificationChannel(context: Context) {
        val name = "Pill Watch Channel"
        val descriptionText = "Notification channel for the PillWatchApp"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("pill_watch_channel", name, importance).apply {
            description = descriptionText
        }
        channel.enableVibration(true)
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}