package com.example.pillwatch.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.di.LoggedUserScope
import com.example.pillwatch.user.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel class for the Splash screen.
 *
 * @param userManager The user manager to access user-related information.
 * @param userRepository The repository for user-related database operations.
 */
@LoggedUserScope
class SplashViewModel @Inject constructor(
    private val userManager: UserManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _welcomeMessage = MutableLiveData<String>()
    val welcomeMessage: LiveData<String>
        get() = _welcomeMessage

    private val _navigationCheck = MutableLiveData<Boolean?>()
    val navigationCheck: LiveData<Boolean?>
        get() = _navigationCheck

    private val _userInDb = MutableLiveData<Boolean?>()
    val userInDb: LiveData<Boolean?>
        get() = _userInDb

    private val _uiStatus = MutableLiveData<Boolean?>()
    val uiStatus: LiveData<Boolean?>
        get() = _uiStatus

    /**
     * Sets the welcome message for the user and triggers a delay before enabling the UI.
     * The welcome message includes the username retrieved from the user manager.
     */
    fun setMessage(message: String) {
        viewModelScope.launch {
            _username.value = userManager.username
            _welcomeMessage.value = "$message ${_username.value}"
            delay(2000)
            _uiStatus.value = true
        }
    }

    /**
     * Signals the start of the navigation process to the main screen.
     */
    fun navigationStart() {
        _navigationCheck.postValue(true)
    }

    /**
     * Signals the completion of the navigation process.
     * Resets the navigation check and UI status values.
     */
    fun navigationComplete() {
        _navigationCheck.value = null
        _uiStatus.value = null
    }

    /**
     * Checks if the user exists in the database.
     * Retrieves the user from the database using the user ID from the user manager.
     * Updates the userInDb value to indicate whether the user exists or not.
     */
    fun checkUserExistsInDb() {
        viewModelScope.launch {
            _userInDb.value = withContext(Dispatchers.IO) {
                val user = userRepository.getUserById(userManager.id)
                user != null
            }
        }
    }
}