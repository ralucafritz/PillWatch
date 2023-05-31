package com.example.pillwatch.storage

import android.content.Context
import javax.inject.Inject

class SharedPreferencesStorage @Inject constructor(context: Context) : Storage {
    private val sharedPreferences = context.getSharedPreferences("Dagger", Context.MODE_PRIVATE)

    override fun setString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    override fun setLong(key: String, value: Long) {
        with(sharedPreferences.edit()) {
            putLong(key, value)
            apply()
        }
    }

    override fun setBoolean(key: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    override fun setInt(key: String, value: Int) {
        with(sharedPreferences.edit()) {
            putInt(key, value)
            apply()
        }
    }

    override fun getString(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }

    override fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, 0L)
    }

    override fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    override fun getInt(key:String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    override fun setTheme(theme: String) {
        setString("theme", theme)
    }

    override fun getTheme(): String {
        return sharedPreferences.getString("theme", "system")!!
    }

    override fun setMessageIndex(index: Int) {
        setInt("messageIndex", index)
    }

    override fun getMessageIndex(): Int {
        return getInt("messageIndex")
    }

    override fun setAlarmNotificationMessage(message: String) {
        setString("alarmNotificationMessage", message)
    }

    override fun getAlarmNotificationMessage(): String {
         return sharedPreferences.getString("alarmNotificationMessage", "Time to take your medication:")!!
    }

    override fun getLanguage(): String {
        return sharedPreferences.getString("language", "ro")!!
    }

    override fun setLanguage(language: String) {
        setString("language", language)
    }
}