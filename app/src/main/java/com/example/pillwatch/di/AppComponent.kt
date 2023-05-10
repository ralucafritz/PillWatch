package com.example.pillwatch.di

import android.content.Context
import com.example.pillwatch.alarms.AlarmReceiver
//import com.example.pillwatch.alarms.NotificationActionReceiver
import com.example.pillwatch.ui.alarms.AlarmsPerDayFragment
import com.example.pillwatch.ui.login.LoginComponent
import com.example.pillwatch.ui.medication.medpage.MedPageFragment
import com.example.pillwatch.ui.signup.SignupComponent
import com.example.pillwatch.user.UserManager
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        StorageModule::class,
        RepositoryModule::class,
        DatabaseModule::class,
        AppSubcomponents::class,
        AlarmsModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun signupComponent(): SignupComponent.Factory
    fun loginComponent(): LoginComponent.Factory
    fun userManager(): UserManager

    fun inject(fragment: AlarmsPerDayFragment)
    fun inject(fragment: MedPageFragment)
    fun inject(alarmReceiver: AlarmReceiver)
//    fun inject(notificationActionReceiver: NotificationActionReceiver)
}