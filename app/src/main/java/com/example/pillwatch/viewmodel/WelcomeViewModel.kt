package com.example.pillwatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.pillwatch.R

class WelcomeViewModel : ViewModel(){

    fun navigateToSignup(navController: NavController) {
        navController.navigate(R.id.action_welcomeFragment_to_signupActivity)
    }

    fun navigateToLogin(navController: NavController) {
        navController.navigate(R.id.action_welcomeFragment_to_loginActivity)
    }
    fun navigateToRandom(navController: NavController) {
        navController.navigate(R.id.action_welcomeFragment_to_testMedsFragment)
    }

}