package com.example.pillwatch.di

import android.content.Context
import com.example.pillwatch.ui.login.LoginComponent
import com.example.pillwatch.ui.signup.SignupComponent
import com.example.pillwatch.ui.welcome.WelcomeFragment
import com.example.pillwatch.user.UserManager
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [StorageModule::class, RepositoryModule::class, DatabaseModule::class, AppSubcomponents::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun signupComponent(): SignupComponent.Factory
    fun loginComponent(): LoginComponent.Factory
    fun userManager(): UserManager
}