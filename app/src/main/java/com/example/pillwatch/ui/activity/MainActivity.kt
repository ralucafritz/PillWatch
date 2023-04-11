package com.example.pillwatch.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pillwatch.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
    }
}