package com.example.pillwatch.user

import com.example.pillwatch.alarms.AlarmHandler
import com.example.pillwatch.alarms.AlarmSchedulerWorker
import com.example.pillwatch.di.LoggedUserScope
import com.example.pillwatch.ui.addmed.AddMedFragment
import com.example.pillwatch.ui.home.HomeFragment
import com.example.pillwatch.ui.splash.SplashActivity
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.ui.medication.MedicationFragment
import com.example.pillwatch.ui.settings.SettingsFragment
import dagger.Subcomponent

@LoggedUserScope
@Subcomponent
interface UserComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): UserComponent
    }

    fun inject(activity: MainActivity)
    fun inject(activity: SplashActivity)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: MedicationFragment)
    fun inject(fragment: AddMedFragment)
    fun inject(alarmSchedulerWorker: AlarmSchedulerWorker)
    fun  inject(alarmHandler: AlarmHandler)
}