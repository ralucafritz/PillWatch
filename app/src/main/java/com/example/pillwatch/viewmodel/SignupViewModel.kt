package com.example.pillwatch.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pillwatch.network.ValidationProperty
import com.example.pillwatch.utils.FirebaseUtils.firebaseAuth
import com.example.pillwatch.utils.FirebaseUtils.firebaseUser
import timber.log.Timber

class SignupViewModel() : ViewModel() {

    companion object {
        const val TAG = "Auth"
        const val INVALID_EMAIL_ERR = "Invalid email."
        const val SHORT_PSW_ERR = "Password is too short, it needs a minimum of 8 characters."
        const val MATCHING_PSW_ERR = "The passwords need to match."
        const val EMPTY_FIELDS_ERR = "Please fill all the fields."
    }

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String>
        get() = _confirmPassword

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setConfirmedPassword(confirmedPassword: String) {
        _confirmPassword.value = confirmedPassword
    }

    fun isValid(): ValidationProperty {
        return if (_email.value != null && _password.value != null && _confirmPassword.value != null) {
             if (Patterns.EMAIL_ADDRESS.matcher(_email.value!!).matches()) {
                if (_password.value!!.length >= 8) {
                    if (_password.value == _confirmPassword.value) {
                        ValidationProperty(true, "")
                    } else {
                        ValidationProperty(false, MATCHING_PSW_ERR)
                    }
                } else {
                    ValidationProperty(false, SHORT_PSW_ERR)
                }
            } else {
                ValidationProperty(false, INVALID_EMAIL_ERR)
            }
        } else {
            ValidationProperty(false, EMPTY_FIELDS_ERR)
        }
    }

    fun signup(): LiveData<Boolean> {
        val email = _email.value!!
        val password = _password.value!!
        val result = MutableLiveData<Boolean>()
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                result.postValue(true)
                Timber.tag(TAG).d("Signup successful: $firebaseUser ")
            } else {
                result.postValue(false)
                Timber.tag(TAG).d("Signup failed.")
            }
        }
        return result
    }

}