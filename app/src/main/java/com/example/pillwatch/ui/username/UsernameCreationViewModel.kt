package com.example.pillwatch.ui.username

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.di.ActivityScope
import com.example.pillwatch.user.UserManager
import com.example.pillwatch.utils.ValidationProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityScope
class UsernameCreationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userManager: UserManager
) : ViewModel() {

    var SHORT_USERNAME = ""
    var EMPTY_FIELDS_ERR = ""

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _updateComplete = MutableLiveData<Boolean>()
    val updateComplete: LiveData<Boolean>
        get() = _updateComplete


    /**
     * Validates the provided username.
     *
     * @param username The username to validate.
     * @return The ValidationProperty indicating whether the username is valid or not.
     */
    fun isValid(username: String): ValidationProperty {
        return when {
            username == "" -> ValidationProperty(false, EMPTY_FIELDS_ERR)
            username.length < 4 -> ValidationProperty(false, SHORT_USERNAME)
            else -> {
                _username.value = username
                ValidationProperty(true, "")
            }
        }
    }

    /**
     * Updates the username for the current user.
     */
    fun updateUsername() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userId = userManager.id
                userRepository.updateUsername(userId, username.value!!)
                userManager.setUsername(username.value!!)
                _updateComplete.postValue(true)
            }
        }
    }
}