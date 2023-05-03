package com.example.pillwatch.ui.addmed

import com.example.pillwatch.data.model.AlarmEntity

interface OnAlarmUpdatedListener {
    fun onAlarmUpdated(alarm: AlarmEntity)
}