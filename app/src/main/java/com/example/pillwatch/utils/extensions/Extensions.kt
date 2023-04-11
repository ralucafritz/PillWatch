package com.example.pillwatch.utils.extensions

import android.app.Activity
import android.widget.Toast
import timber.log.Timber

object Extensions {
    fun Activity.toast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun Activity.timber() {
        Timber.plant(Timber.DebugTree())
    }
}