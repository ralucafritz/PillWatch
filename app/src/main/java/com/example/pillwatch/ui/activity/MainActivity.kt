package com.example.pillwatch.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pillwatch.R
import com.example.pillwatch.utils.extensions.ContextExtensions.setLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.setPreference
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
//        setLoggedInStatus(false)
//        setPreference("email","")
        setContentView(R.layout.activity_main)
    }
}