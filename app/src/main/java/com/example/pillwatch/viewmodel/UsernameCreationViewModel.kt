package com.example.pillwatch.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.pillwatch.R
import com.example.pillwatch.utils.ValidationProperty

class UsernameCreationViewModel(application: Application) : ViewModel() {

    companion object {
        const val TAG = "UsernameCreationVM"
        const val SHORT_USERNAME = "Username is too short. Minimum 4 characters."
        const val EMPTY_FIELDS_ERR = "Please fill all the fields."
    }

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    fun setUsername(usernameInput: String) {
        _username.value = usernameInput
    }

    fun isValid(): ValidationProperty {
       return when {
           _username.value == null -> ValidationProperty(false, EMPTY_FIELDS_ERR)
           _username.value!!.length < 4 -> ValidationProperty(false, SHORT_USERNAME)
           else -> ValidationProperty(true, "")
       }
    }

    fun navigateToHome(navController: NavController) {
        navController.navigate(R.id.action_usernameCreationFragment_to_testMedsFragment)
    }

}