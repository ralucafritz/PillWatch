package com.example.pillwatch.alarms

import com.example.pillwatch.data.model.AlarmEntity

interface AlarmReceiverCallback {
    fun onAlarmFetched(alarm: AlarmEntity)
}