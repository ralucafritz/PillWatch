package com.example.pillwatch.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.pillwatch.R

class HomeViewModel(application: Application): AndroidViewModel(application){

    fun navigateToAddAMed(navController: NavController) {
        navController.navigate(R.id.action_homeFragment_to_addMedFragment)
    }
}