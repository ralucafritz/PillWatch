package com.example.pillwatch.utils.extensions

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
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