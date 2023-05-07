package com.example.pillwatch.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.ui.alarms.AlarmsViewModel
import com.example.pillwatch.utils.TakenStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as PillWatchApplication).appComponent.inject(this)


        val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L)

        if (alarmId == -1L) {
            Timber.e("Invalid alarm ID")
            return
        }

        /**
         *  get AlarmEntity from the db using alarmId
         * */
        CoroutineScope(Dispatchers.IO).launch {
            val alarm = alarmRepository.getAlarmById(alarmId)

            // Show the notification
            if (alarm != null) {
                alarmScheduler.showNotification(context, alarm)
            }
        }
    }
}