package com.example.pillwatch.utils

enum class AlarmTiming(val label: String) {
    EVERY_X_HOURS("Every X hours"),
    ONCE_A_DAY("Once a day"),
    TWICE_A_DAY("Twice a day"),
    THREE_TIMES("Three times a day"),
    FOUR_TIMES("Four times a day"),
    NO_REMINDERS("No reminder needed")
}