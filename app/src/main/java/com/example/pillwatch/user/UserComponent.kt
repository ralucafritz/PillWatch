package com.example.pillwatch.user

import com.example.pillwatch.di.LoggedUserScope
import com.example.pillwatch.ui.home.HomeFragment
import com.example.pillwatch.ui.splash.SplashActivity
import com.example.pillwatch.ui.main.MainActivity
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
}