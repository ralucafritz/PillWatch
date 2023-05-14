package com.example.pillwatch.alarms

import com.example.pillwatch.data.model.AlarmEntity

interface OnAlarmUpdatedListener {
    fun onAlarmUpdated(updatedAlarm: AlarmEntity)
}