package com.example.pillwatch.ui.login

import com.example.pillwatch.di.ActivityScope
import com.example.pillwatch.ui.username.UsernameCreationFragment
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface LoginComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    fun inject(activity: LoginActivity)
    fun inject(fragment: UsernameCreationFragment)

}