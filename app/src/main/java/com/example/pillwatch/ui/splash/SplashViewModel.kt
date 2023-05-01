package com.example.pillwatch.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.di.LoggedUserScope
import com.example.pillwatch.user.UserDataRepository
import com.example.pillwatch.user.UserManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@LoggedUserScope
class SplashViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {

    companion object {
        const val TAG = "Loading"
    }

    private val _loggedInStatus = MutableLiveData<Boolean>()
    val loggedInStatus: LiveData<Boolean>
        get() = _loggedInStatus

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _welcomeMessage = MutableLiveData<String>()
    val welcomeMessage: LiveData<String>
        get() = _welcomeMessage

    private val _navigationCheck = MutableLiveData<Boolean?>()
    val navigationCheck: LiveData<Boolean?>
        get() = _navigationCheck

    fun setMessage() {
        viewModelScope.launch {
            _username.value = userManager.username
            Timber.tag(TAG).d(_username.value!!)
            _welcomeMessage.value = "Welcome ${_username.value}"
            delay()
            _navigationCheck.value = true
        }
    }

    fun delay() {
        viewModelScope.launch {
            delay(2000)
        }
    }

    fun navigationComplete() {
        _navigationCheck.value = null
    }
}