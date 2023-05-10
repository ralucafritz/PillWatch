package com.example.pillwatch.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.utils.TakenStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

//class NotificationActionReceiver : BroadcastReceiver() {
//    @Inject
//    lateinit var alarmRepository: AlarmRepository
//
//    @Inject
//    lateinit var alarmHandler: AlarmHandler
//
//    override fun onReceive(context: Context, intent: Intent) {
//        (context.applicationContext as PillWatchApplication).appComponent.inject(this)
//        val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L)
//        if (alarmId != -1L) {
//            when (intent.action) {
//                AlarmScheduler.ACTION_TAKEN -> {
//                    alarmHandler.createMedsLog(alarmId, TakenStatus.TAKEN)
//                }
//
//                AlarmScheduler.ACTION_POSTPONED -> {
//                    alarmHandler.createMedsLog(alarmId, TakenStatus.POSTPONED)
//                    val alarmScheduler = AlarmScheduler(context)
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val alarm = alarmRepository.getAlarmById(alarmId)
//                        alarm.timeInMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)
//                        alarmScheduler.scheduleAlarm(alarm)
//                    }
//                }
//
//                AlarmScheduler.ACTION_MISSED -> {
//                    alarmHandler.createMedsLog(alarmId, TakenStatus.MISSED)
//                }
//            }
//        }
//    }
//}