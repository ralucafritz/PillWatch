package com.example.pillwatch

import android.app.Application
import com.example.pillwatch.di.AppComponent
import com.example.pillwatch.di.DaggerAppComponent

class PillWatchApplication : Application(){

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

}