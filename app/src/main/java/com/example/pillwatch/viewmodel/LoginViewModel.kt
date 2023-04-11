package com.example.pillwatch.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pillwatch.network.ValidationProperty
import com.example.pillwatch.utils.FirebaseUtils.firebaseAuth
import com.example.pillwatch.utils.FirebaseUtils.firebaseUser
import timber.log.Timber

class LoginViewModel: ViewModel() {

    companion object {
        const val TAG = "Auth"
        const val INVALID_EMAIL_ERR = "Invalid email."
        const val SHORT_PSW_ERR = "Password is too short, it needs a minimum of 8 characters."
        const val EMPTY_FIELDS_ERR = "Please fill all the fields."
    }

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun isValid(): ValidationProperty {
        return if (_email.value != null && _password.value != null) {
            if (Patterns.EMAIL_ADDRESS.matcher(_email.value!!).matches()) {
                if (_password.value!!.length >= 8) {
                    ValidationProperty(true, "")
                } else {
                    ValidationProperty(false, SHORT_PSW_ERR)
                }
            } else {
                ValidationProperty(false, INVALID_EMAIL_ERR)
            }
        } else {
            ValidationProperty(false,  EMPTY_FIELDS_ERR)
        }
    }

        fun login(): LiveData<Boolean> {
            val email = _email.value!!
            val password = _password.value!!
            val result = MutableLiveData<Boolean>()
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful) {
                    result.postValue(true)
                    Timber.tag(TAG).d("Login successful: $firebaseUser")
                } else {
                    result.postValue(false)
                    Timber.tag(TAG).d("Login failed.")
                }
            }

            return result
        }

}