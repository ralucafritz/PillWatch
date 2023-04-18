package com.example.pillwatch.utils.extensions

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pillwatch.utils.SharedPreferencesUtil
import com.example.pillwatch.utils.extensions.ContextExtensions.setLoggedInStatus
import timber.log.Timber

object Extensions {
    fun Activity.toast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun Activity.timber() {
        Timber.plant(Timber.DebugTree())
    }
}