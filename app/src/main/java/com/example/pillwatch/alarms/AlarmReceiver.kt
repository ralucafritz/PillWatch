package com.example.pillwatch.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.ui.alarms.AlarmsViewModel
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var viewModel: AlarmsViewModel

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as PillWatchApplication).appComponent.inject(this)


        val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L)

        /**
         *  get AlarmEntity from the db using alarmId
         * */
        viewModel.getAlarmById(alarmId, object : AlarmReceiverCallback {
            override fun onAlarmFetched(alarm: AlarmEntity) {
                alarm.let {
                    val alarmScheduler = AlarmScheduler(context)
                    alarmScheduler.showNotification(context, it)
                }
            }
        })
    }
}