package com.example.pillwatch.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pillwatch.R
import com.example.pillwatch.utils.extensions.ContextExtensions.getLoggedInStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingViewModel(application : Application): ViewModel() {

    private val context: Context by lazy { application.applicationContext }

    private val _loggedInStatus = MutableLiveData<Boolean>()
    val loggedInStatus: LiveData<Boolean>
        get() = _loggedInStatus


    fun checkLoginStatus() {
        viewModelScope.launch {
            delay(2000)
            _loggedInStatus.value = context.getLoggedInStatus()
        }
    }

    fun navigateToWelcome(navController: NavController) {
        navController.navigate(R.id.action_loadingFragment_to_welcomeFragment)
    }

    fun navigateToHome(navController: NavController) {
        navController.navigate(R.id.action_loadingFragment_to_testMedsFragment)
    }

}