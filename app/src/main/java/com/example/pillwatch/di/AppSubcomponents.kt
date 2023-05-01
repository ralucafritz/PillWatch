package com.example.pillwatch.di

import com.example.pillwatch.ui.login.LoginComponent
import com.example.pillwatch.ui.signup.SignupComponent
import com.example.pillwatch.user.UserComponent
import dagger.Module

@Module(subcomponents = [SignupComponent::class, LoginComponent::class, UserComponent::class])
class AppSubcomponents {
}