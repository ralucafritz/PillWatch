package com.example.pillwatch.utils.extensions

import android.app.Activity
import android.widget.Toast
import timber.log.Timber

object Extensions {
    /**
     *
     *      Extension function to show a toast message
     *      @param msg is the message that is about to be showed
     *
     */
    fun Activity.toast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    /**
     *
     *      Extension function that enables Timber logs in that activity
     *
     */
    fun timber() {
        Timber.plant(Timber.DebugTree())
    }
}