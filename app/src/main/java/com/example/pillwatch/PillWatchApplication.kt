package com.example.pillwatch

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pillwatch.alarms.AlarmSchedulerWorker
import com.example.pillwatch.di.AppComponent
import com.example.pillwatch.di.DaggerAppComponent
import java.util.Calendar
import java.util.concurrent.TimeUnit

class PillWatchApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel(this)
//        scheduleOneTimeAlarmSchedulerWorker(this)
//        scheduleAlarmSchedulerWorker(this)
    }

    private fun scheduleAlarmSchedulerWorker(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // calculate the time until the next even hour
        val now = Calendar.getInstance()
        val nextHour = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val initialDelay = nextHour.timeInMillis - now.timeInMillis

        // create a PeriodicWorkRequest for AlarmSchedulerWorker
        val alarmSchedulerWorkerRequest =
            PeriodicWorkRequestBuilder<AlarmSchedulerWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .addTag("alarmSchedulerWorker")
                .build()

        // schedule the worker
        workManager.enqueueUniquePeriodicWork(
            "AlarmSchedulerWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
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

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleOneTimeAlarmSchedulerWorker(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // create a OneTimeWorkRequest for AlarmSchedulerWorker
        val oneTimeAlarmSchedulerWorkerRequest = OneTimeWorkRequestBuilder<AlarmSchedulerWorker>()
            .addTag("oneTimeAlarmSchedulerWorker")
            .build()

        // schedule the worker
        workManager.enqueueUniqueWork(
            "OneTimeAlarmSchedulerWorker",
            ExistingWorkPolicy.REPLACE,
            oneTimeAlarmSchedulerWorkerRequest
        )
    }

}