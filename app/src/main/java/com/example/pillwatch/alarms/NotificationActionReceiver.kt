package com.example.pillwatch.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.utils.TakenStatus
import javax.inject.Inject

class NotificationActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmHandler: AlarmHandler

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as PillWatchApplication).appComponent.inject(this)

        when (intent.action) {
            AlarmScheduler.ACTION_TAKEN -> {
                val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L)
                if (alarmId != -1L) {
                    alarmHandler.createMedsLog(alarmId, TakenStatus.TAKEN)
                }
            }

            AlarmScheduler.ACTION_POSTPONED -> {
                val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L)
                if (alarmId != -1L) {
                    alarmHandler.createMedsLog(alarmId, TakenStatus.POSTPONED)
                }
            }

            AlarmScheduler.ACTION_MISSED -> {
                val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L)
                if (alarmId != -1L) {
                    alarmHandler.createMedsLog(alarmId, TakenStatus.MISSED)
                }
            }
        }
    }
}