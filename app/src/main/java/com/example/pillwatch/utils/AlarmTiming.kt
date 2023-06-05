package com.example.pillwatch.utils

import com.example.pillwatch.R

enum class AlarmTiming(val labelResId: Int) {
    EVERY_X_HOURS(R.string.every_x_hours),
    ONCE_A_DAY(R.string.once_a_day),
    TWICE_A_DAY(R.string.twice_a_day),
    THREE_TIMES(R.string.three_times),
    FOUR_TIMES(R.string.four_times),
    NO_REMINDERS(R.string.no_reminders),
}