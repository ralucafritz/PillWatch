package com.example.pillwatch.alarms

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.user.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlarmSchedulerWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var userMedsRepository: UserMedsRepository

    @Inject
    lateinit var alarmHandler: AlarmHandler

    @Inject
    lateinit var alarmGenerator: AlarmGenerator

    private lateinit var userManager: UserManager

    init {
        val userManager = (context as PillWatchApplication).appComponent.userManager()
        userManager.userComponent!!.inject(this)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // get all UserMedEntities from the database
            val userMedEntities = userMedsRepository.getAllMedsForUser(userManager.id)
            val currentTime = System.currentTimeMillis()

            // check if the current time is past the last alarm for each UserMedEntity
            userMedEntities.forEach { userMed ->
                val lastAlarm = alarmRepository.getLastAlarmByMedId(userMed.medId!!)

                if (currentTime > lastAlarm.timeInMillis) {
                    // the current time is past the last alarm => regenerate alarms
                    val newAlarmList = alarmGenerator.generateAlarms(
                        lastAlarm.alarmTiming,
                        lastAlarm.medId,
                        lastAlarm.everyXHours ?: 1,
                        lastAlarm.timeInMillis
                    )

                    alarmRepository.clearForMedId(userMed.id)
                    alarmRepository.insertAll(newAlarmList)
                }
            }

            // get all the current alarms from the database
            val alarms = alarmRepository.getAllAlarms()

            // schedule the alarms that are active
            alarms.filter { it.isEnabled && it.timeInMillis > currentTime}.forEach { alarm ->
                alarmHandler.scheduleAlarm(alarm.id.toInt(), alarm.timeInMillis)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}