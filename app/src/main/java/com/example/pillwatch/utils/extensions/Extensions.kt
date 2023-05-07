package com.example.pillwatch.utils.extensions

import timber.log.Timber

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