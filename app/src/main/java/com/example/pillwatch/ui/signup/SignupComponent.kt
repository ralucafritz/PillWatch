package com.example.pillwatch.ui.signup

import com.example.pillwatch.di.ActivityScope
import com.example.pillwatch.ui.username.UsernameCreationFragment
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface SignupComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SignupComponent
    }

    fun inject(activity: SignupActivity)
    fun inject(fragment: UsernameCreationFragment)
}