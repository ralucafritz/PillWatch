package com.example.pillwatch.ui.username

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.pillwatch.R
import com.example.pillwatch.data.source.local.UserDao
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.utils.ValidationProperty
import com.example.pillwatch.utils.extensions.ContextExtensions.getPreference
import com.example.pillwatch.utils.extensions.ContextExtensions.setPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class UsernameCreationViewModel(userDao: UserDao, application: Application) : AndroidViewModel(application) {

    private val context: Context by lazy { application.applicationContext }

    companion object {
        const val TAG = "UsernameCreationVM"
        const val SHORT_USERNAME = "Username is too short. Minimum 4 characters."
        const val EMPTY_FIELDS_ERR = "Please fill all the fields."
    }

    private val repository = UserRepository(userDao)

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

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
        viewModelScope.launch{
                update()
        }
    }

    suspend fun update() {
        val userId = context.getPreference("id", 0L).toString().toLong()
        withContext(Dispatchers.IO) {
            repository.updateUserName(userId,
                _username.value!!.lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
        }
        context.setPreference("username", _username.value!!)
    }

    fun navigateToHome(navController: NavController) {
        navController.navigate(R.id.action_usernameCreationFragment_to_homeFragment)
    }

}