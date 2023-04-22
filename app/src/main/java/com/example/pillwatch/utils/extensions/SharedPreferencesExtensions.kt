package com.example.pillwatch.utils.extensions

import android.content.Context
import androidx.preference.PreferenceManager

object SharedPreferencesExtensions {

    inline fun <reified  T> getPreference(context: Context, key: String, defaultValue: T) : T {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            is Long -> sharedPreferences.getLong(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported preference value type")
        }
    }

    fun setPreference(context: Context, key: String, value: Any) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is Boolean -> editor.putBoolean(key, value)
            else -> throw IllegalArgumentException("Unsupported preference value type")
        }
        editor.apply()
    }



}
