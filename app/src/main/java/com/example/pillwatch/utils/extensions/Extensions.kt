package com.example.pillwatch.utils.extensions

import timber.log.Timber
import java.util.Locale

object Extensions {
    /**
     *
     *      Extension function that enables Timber logs in that activity
     *
     */
    fun timber() {
        Timber.plant(Timber.DebugTree())
    }

}