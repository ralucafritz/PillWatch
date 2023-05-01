package com.example.pillwatch.ui.username

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pillwatch.data.source.local.UserDao
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.di.ActivityScope
import com.example.pillwatch.user.UserManager
import com.example.pillwatch.utils.ValidationProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@ActivityScope
class UsernameCreationViewModel @Inject constructor(
    private val repository: UserRepository,
    private val userManager: UserManager
) : ViewModel() {

    companion object {
        const val TAG = "UsernameCreationVM"
        const val SHORT_USERNAME = "Username is too short. Minimum 4 characters."
        const val EMPTY_FIELDS_ERR = "Please fill all the fields."
    }

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _updateComplete = MutableLiveData<Boolean>()
    val updateComplete: LiveData<Boolean>
        get() = _updateComplete

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

    fun updateUsername() {
        viewModelScope.launch {
            val userId = userManager.id
            withContext(Dispatchers.IO) {
                repository.updateUserName(userId,
                    username.value!!)
            }
            userManager.setUsername(username.value!!)
            Timber.tag("USERMANAGER").d(userManager.isUserLoggedIn().toString())
            _updateComplete.value = true
        }
    }
}