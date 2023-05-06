package com.example.pillwatch.alarms

import com.example.pillwatch.data.model.MedsLogEntity
import com.example.pillwatch.data.repository.AlarmRepository
import com.example.pillwatch.data.repository.MedsLogRepository
import com.example.pillwatch.utils.TakenStatus
import javax.inject.Inject

class AlarmHandler @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val medsLogRepository: MedsLogRepository
) {

    fun createMedsLog(alarmId: Long, status: TakenStatus) {
        val alarm = alarmRepository.getAlarmById(alarmId)

        val medId = alarm.medId
        val timestamp = System.currentTimeMillis()
        val medsLog = MedsLogEntity(medId = medId, status = status, timestamp = timestamp)
        medsLogRepository.insert(medsLog)
    }
}