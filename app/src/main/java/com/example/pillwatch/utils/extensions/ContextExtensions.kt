package com.example.pillwatch.utils.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.pillwatch.utils.SharedPreferencesUtil

object ContextExtensions {

    fun Context.setLoggedInStatus(value: Boolean) {
        SharedPreferencesUtil.setPreference(this, "isLoggedIn", value)
    }

    fun Context.getLoggedInStatus() : Boolean {
        return SharedPreferencesUtil.getPreference(this, "isLoggedIn", false)
    }

    fun Context.setPreference(key: String, value: Any) {
        SharedPreferencesUtil.setPreference(this, key, value)
    }

    fun Context.getPreference(key: String, type: Any) : Any {
        return SharedPreferencesUtil.getPreference(this, key, type)
    }

    fun Context.getPreference(key: String) : String {
        return SharedPreferencesUtil.getPreference(this, key, "")
    }

    fun Context.isInternetConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}