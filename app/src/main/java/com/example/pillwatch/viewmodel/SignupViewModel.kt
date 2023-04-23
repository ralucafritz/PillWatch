package com.example.pillwatch.viewmodel

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.lifecycle.*
import com.example.pillwatch.data.datasource.local.UserDao
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.utils.AuthResultProperty
import com.example.pillwatch.utils.extensions.FirebaseUtils.firebaseUser
import com.example.pillwatch.utils.ValidationProperty
import com.example.pillwatch.utils.extensions.ContextExtensions.isInternetConnected
import com.example.pillwatch.utils.extensions.ContextExtensions.setLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.setPreference
import com.example.pillwatch.utils.extensions.ContextExtensions.toast
import com.example.pillwatch.utils.extensions.FirebaseUtils.firebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SignupViewModel(
    userDao: UserDao,
    application: Application
) : AndroidViewModel(application) {

    private val context: Context by lazy { application.applicationContext }

    private val userRepository = UserRepository(userDao)

    // start the job
    private var viewModelJob = Job()

    // coroutine scope for main thread
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // SIGN UP WITH EMAIL AND PASSWORD
    companion object {
        const val TAG = "Auth"
        const val INVALID_EMAIL_ERR = "Invalid email."
        const val EMAIL_TAKEN_ERR = "Email already in use."
        const val SHORT_PWD_ERR = "Password is too short. Minimum 8 characters."
        const val MATCHING_PWD_ERR = "The passwords need to match."
        const val EMPTY_FIELDS_ERR = "Please fill all the fields."
        const val SIGNUP_SUCCESS = "Signup successful."
        const val SIGNUP_FAIL = "Signup failed."
        const val GOOGLE = "Google"
        const val FIREBASE = "Firebase"
    }

    private val _signupResult = MutableLiveData<Boolean>()
    val signupResult: LiveData<Boolean>
        get() = _signupResult

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String>
        get() = _confirmPassword

    private val _firebaseListener = MutableLiveData<Boolean>()
    val firebaseListener: LiveData<Boolean>
        get() = _firebaseListener

    fun isValid(email: String, password: String, confirmPassword: String): ValidationProperty {
        return when {
            email == "" || password == "" || confirmPassword == "" -> ValidationProperty(
                false,
                EMPTY_FIELDS_ERR
            )

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationProperty(
                false,
                INVALID_EMAIL_ERR
            )

            password.length < 8 -> ValidationProperty(
                false,
                SHORT_PWD_ERR
            )

            password != confirmPassword -> ValidationProperty(
                false,
                MATCHING_PWD_ERR
            )

            else -> {
                _email.value = email
                _password.value = password
                _confirmPassword.value = confirmPassword
                ValidationProperty(true, "")
            }
        }
    }

    fun signup() {
        coroutineScope.launch {
            // set local variables for email and password for easier use
            val email = _email.value!!
            val password = _password.value!!

            // if there is internet connection => check firebase if the user can be created
            if (context.isInternetConnected()) {
                // create user in firebase
                try {
                    withContext(Dispatchers.IO) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                            if (it.isSuccessful) {
                                _firebaseListener.postValue(true)
                                Timber.tag(TAG).d("$FIREBASE $SIGNUP_SUCCESS: $firebaseUser ")
                            } else {
                                _firebaseListener.postValue(false)
                                Timber.tag(TAG).d("$FIREBASE $SIGNUP_FAIL")
                            }
                        }
                    }
                } catch (e: Exception) {
                    /**
                     * failed -> log the error
                     * depending on the error show an alert
                     * set status to false
                     * */
                    val exceptionMessage = handleFirebaseException(e)
                    errorSignup(FIREBASE, exceptionMessage)
                }
            }
            // if the value of  @_signupResult is false => leave the coroutine  and function
            // aka signup failed

            if (_signupResult.value == false) {
                return@launch
            }

            // otherwise, start the process of adding the user to the local database
            val user = withContext(Dispatchers.IO) {
                userRepository.signup(email, password)
            }
            /**
             * if the user != null => was created successfully
             * set the login status to true
             * save the email for easier use
             * save the id for easier use
             **/
            if (user != null) {
                setLoggedInStatus(user.email, user.id)
                _signupResult.postValue(true)
                Timber.tag(TAG).d("$SIGNUP_SUCCESS: $user ")
            } else {
                // user == null  => user is already in the database
                // set the login status to false
                // show error alert
                // log the error
                errorSignup("", EMAIL_TAKEN_ERR)
            }
        }
    }


    // SIGN UP WITH GOOGLE
    fun signupWithGoogle(signInAccountTask: Task<GoogleSignInAccount>) {
        coroutineScope.launch {
            try {
                // Set the logged-in status and post the successful result
                val account = signInAccountTask.getResult(ApiException::class.java)
                val res = insertWithGoogle(account.idToken ?: "")
                _signupResult.postValue(res)
            } catch (err: ApiException) {
                _signupResult.postValue(false)
                Timber.tag(TAG).e("$GOOGLE $SIGNUP_FAIL: $err")
            }
        }
    }

    private suspend fun insertWithGoogle(idToken: String): Boolean {
        return withContext(Dispatchers.Main + viewModelJob) {
            if (idToken == "") {
                false
            } else {
                // get the Google credential and sign in
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                withContext(Dispatchers.IO) {
                    val authResult = signInWithCredential(credential)
                    if (authResult.success && authResult.email != null) {
                        // Ii the sign in is successful, insert the user into the database
                        val email = authResult.email!!
                        val user = userRepository.signup(email)
                        if (user != null) {
                            /**
                             * if the user != null => was created/ logged in  successfully
                             * set the login status to true
                             * save the email for easier use
                             * save the id for easier use
                             * save the username for easier use
                             **/
                            setLoggedInStatus(email, user.id, user.username)
                            Timber.tag(TAG).w("$GOOGLE $SIGNUP_SUCCESS")
                            true
                        } else {
                            context.toast("An error occurred.")
                            false
                        }

                    } else {
                        context.toast("An error occurred")
                        false
                    }
                }
            }
        }
    }

    private suspend fun signInWithCredential(
        credential: AuthCredential
    ): AuthResultProperty {
        return suspendCoroutine { continuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(AuthResultProperty(true, task.result, task.result.user!!.email))
                    } else {
                        continuation.resume(AuthResultProperty(false, null, null))
                    }
                }
        }
    }

    private fun setLoggedInStatus(email: String, id: Long, username: String? = null) {
        context.setLoggedInStatus(true)
        context.setPreference("id", id)
        context.setPreference("email", email)
        if (username != null) {
            context.setPreference("username", username)
        }
    }

    private fun handleFirebaseException(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthUserCollisionException -> {
                EMAIL_TAKEN_ERR
            }

            else -> {
                exception!!.stackTraceToString()
            }
        }
    }


    private fun errorSignup(str: String = "", error: String = "") {
        context.toast(error)
        _signupResult.postValue(false)

        var errMsg = "$str $SIGNUP_FAIL "

        if (error != "") {
            errMsg += error
        }

        Timber.tag(TAG).e(errMsg)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}