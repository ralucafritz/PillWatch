package com.example.pillwatch.ui.loading

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pillwatch.R
import com.example.pillwatch.utils.extensions.ContextExtensions.getLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.getPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class LoadingViewModel(application : Application): AndroidViewModel(application) {

    companion object {
        const val TAG = "Loading"
    }

    private val context: Context by lazy { application.applicationContext }

    private val _loggedInStatus = MutableLiveData<Boolean>()
    val loggedInStatus: LiveData<Boolean>
        get() = _loggedInStatus

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _welcomeMessage = MutableLiveData<String>()
    val welcomeMessage: LiveData<String>
        get() = _welcomeMessage

    fun checkLoginStatus() {
        viewModelScope.launch {
            _username.value = context.getPreference("username")
            Timber.tag(TAG).d(_username.value!!)
            val usernameToPrint = _username.value
            _welcomeMessage.value = "Welcome${" $usernameToPrint"}"
            delay(2000)
            _loggedInStatus.value = context.getLoggedInStatus()
        }
    }

    fun navigateToWelcome(navController: NavController) {
        navController.navigate(R.id.action_loadingFragment_to_welcomeFragment)
    }
    fun navigateToHome(navController: NavController) {
        navController.navigate(R.id.action_loadingFragment_to_homeFragment)
    }
    fun navigateToUsernameCreate(navController: NavController) {
        navController.navigate(R.id.action_loadingFragment_to_usernameCreationFragment)
    }

}