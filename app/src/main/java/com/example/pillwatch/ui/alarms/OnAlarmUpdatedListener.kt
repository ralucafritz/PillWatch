package com.example.pillwatch.ui.alarms

import com.example.pillwatch.data.model.AlarmEntity

interface OnAlarmUpdatedListener {
    fun onAlarmUpdated(alarm: AlarmEntity)
}