package com.example.pillwatch.alarms

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.UserMedsRepository
import com.example.pillwatch.user.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AlarmSchedulerWorker @Inject constructor(
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

    private var userManager: UserManager =
        (context as PillWatchApplication).appComponent.userManager()

    init {
        userManager.userComponent!!.inject(this)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // get all UserMedEntities from the database
            val userMedEntities = userMedsRepository.getAllMedsForUser(userManager.id)
            val currentTime = System.currentTimeMillis()

            // get all the current alarms from the database
            val alarms = alarmRepository.getAllAlarms()

            // check if the current time is past the last alarm for each UserMedEntity
            userMedEntities.forEach { userMed ->
                if(!userMed.isArchived) {
                    scheduleAndGenerateAlarmsForMed(userMed.id, alarms, currentTime)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Timber.tag("WORKER").e(e)
            Result.failure()
        }
    }

    private fun scheduleAndGenerateAlarmsForMed(medId: String, alarms: List<AlarmEntity>, currentTime: Long) {
        val medAlarms = alarms.filter { it.medId == medId && it.isEnabled }

        if (medAlarms.isNotEmpty()) {
            val lastAlarm = medAlarms.maxByOrNull { it.timeInMillis }
            if (lastAlarm != null) {
                if (currentTime > lastAlarm.timeInMillis) {
                    alarmHandler.scheduleNextAlarms(medId)

                } else {
                    medAlarms.forEach {
                        if (currentTime < it.timeInMillis) {
                            alarmHandler.rescheduleAlarm(it)
                        }
                    }
                }
            }
        }
    }
}