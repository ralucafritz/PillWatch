package com.example.pillwatch.ui.login

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pillwatch.data.source.local.UserDao
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.utils.AuthResultProperty
import com.example.pillwatch.utils.extensions.FirebaseUtils.firebaseAuth
import com.example.pillwatch.utils.ValidationProperty
import com.example.pillwatch.utils.checkPassword
import com.example.pillwatch.utils.extensions.ContextExtensions.isInternetConnected
import com.example.pillwatch.utils.extensions.ContextExtensions.setLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.setPreference
import com.example.pillwatch.utils.extensions.ContextExtensions.showAlert
import com.example.pillwatch.utils.extensions.ContextExtensions.toast
import com.example.pillwatch.ui.signup.SignupViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception
import java.lang.NullPointerException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginViewModel(userDao: UserDao, application: Application) :
    AndroidViewModel(application) {

    private val context: Context by lazy { application.applicationContext }

    private val userRepository = UserRepository(userDao)

    // start the job
    private var viewModelJob = Job()

    // coroutine scope for main thread
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    companion object {
        const val TAG = "Auth"
        const val INVALID_EMAIL_ERR = "Invalid email."
        const val SHORT_PWD_ERR = "Password is too short. Minimum 8 characters."
        const val EMPTY_FIELDS_ERR = "Please fill all the fields."
        const val INCORRECT_PWD = "Incorrect password."
        const val NO_USER = "The user with that email was not found"
        const val LOGIN_SUCCESS = "Login successful."
        const val LOGIN_FAIL = "Login failed."
        const val GOOGLE = "Google"
        const val FIREBASE = "Firebase"
    }

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean>
        get() = _loginResult

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    private val _firebaseListener = MutableLiveData<Boolean>()
    val firebaseListener: LiveData<Boolean>
        get() = _firebaseListener

    fun isValid(email: String, password: String): ValidationProperty {
        return when {
            email == "" || password == "" -> ValidationProperty(
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

            else -> {
                _email.value = email
                _password.value = password
                ValidationProperty(true, "")
            }
        }
    }

    fun login() {
        coroutineScope.launch {
            // set local variables for email and password for easier use
            val email = _email.value!!
            val password = _password.value!!

            // if there is internet connection => check firebase if the user exists and if it can be logged in
            if (context.isInternetConnected()) {
                // get user from firebase
                try {
                    withContext(Dispatchers.IO) {
                        // find user in firebase
                        signInWithEmailAndPassword(email, password)
                    }
                    if (_firebaseListener.value!!) {
                        // successful -> get user from local db
                        val user = userRepository.getUserByEmail(email)
                        if (user != null) {
                            /**
                             * if found
                             * set the login status to true
                             * save the email for easier use
                             * save the id for easier use
                             * save the username for easier use
                             * */
                            setLoggedInStatus(user.email, user.id, user.username)
                            _loginResult.postValue(true)
                            Timber.tag("$FIREBASE $TAG").d("$FIREBASE $LOGIN_SUCCESS $user")
                        } else {
                            errorLogin(FIREBASE, NO_USER)
                            Timber.tag("$FIREBASE $TAG").d("$FIREBASE $NO_USER")
                            TODO("IMPLEMENT WHEN REALTIME DB WORKS")
                        }
                    }
                } catch (e: Exception) {
                    /**
                     * failed -> log the error
                     * depending on the error show an alert
                     * set status to false
                     * */
                    val exceptionMessage = handleFirebaseException(e)
                    errorLogin(SignupViewModel.FIREBASE, exceptionMessage)
                }
            }
            if (_loginResult.value == null) {
                val user = withContext(Dispatchers.IO) { userRepository.getUserByEmail(email) }

                if (user == null) {
                    /**
                     * user == null  => user is already in the database
                     * set the login status to false
                     * show error alert
                     * log the error
                     **/
                    errorLogin("", NO_USER)
                } else if (checkPassword(password, user.password)) {
                    /**
                     * if the user != null && password check passes=> was logged in successfully
                     * set the login status to true
                     * save the email for easier use
                     * save the id for easier use
                     * save the username for easier use -> in this case is null cause it's a new user
                     **/
                    setLoggedInStatus(user.email, user.id, user.username)
                    _loginResult.postValue(true)
                } else {
                    /**
                     * confirm fails => incorrect password
                     * set the login status to false
                     * show error alert
                     * log the error
                     **/
                    errorLogin("", INCORRECT_PWD)
                }
            }
        }
    }

    fun loginWithGoogle(signInAccountTask: Task<GoogleSignInAccount>) {
        coroutineScope.launch {
            try {
                // get the Google account from the sign in task
                val account = signInAccountTask.getResult(ApiException::class.java)
                val res = insertWithGoogle(account.idToken ?: "")
                _loginResult.postValue(res)
            } catch (err: ApiException) {
                _loginResult.postValue(false)
                Timber.tag(TAG).e("$GOOGLE $LOGIN_FAIL: $err")
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
                        // if the sign in is successful, insert the user into the database
                        val email = authResult.email!!
                        val user = userRepository.signup(email)
                        if (user != null) {
                            /**
                             * if the user != null => was created/logged in  successfully
                             * set the login status to true
                             * save the email for easier use
                             * save the id for easier use
                             * save the username for easier use
                             **/
                            setLoggedInStatus(email, user.id, user.username)
                            Timber.tag(TAG).w("$GOOGLE $LOGIN_SUCCESS")
                            true
                        } else {
                            context.showAlert("An error occurred", "Error")
                            false
                        }

                    } else {
                        context.showAlert("An error occurred", "Error")
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
                        continuation.resume(
                            AuthResultProperty(
                                true,
                                task.result,
                                task.result.user!!.email
                            )
                        )
                    } else {
                        continuation.resume(AuthResultProperty(false, null, null))
                    }
                }
        }
    }

    private suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Boolean {
        return suspendCoroutine { continuation ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _firebaseListener.value = true
                        continuation.resume(true)
                    } else {
                        _firebaseListener.value = false
                        continuation.resume(false)
                    }
                }
        }
    }

    private fun handleFirebaseException(exception: Exception?): String {
        return when (exception) {
            // Handles an invalid password exception
            is FirebaseAuthInvalidCredentialsException -> {
                INCORRECT_PWD
            }
            // Handles an invalid user exception
            is FirebaseAuthInvalidUserException -> {
                if (exception.errorCode == "ERROR_USER_NOT_FOUND") {
                    NO_USER
                } else {
                    exception.stackTraceToString()
                }
            }

            is NullPointerException -> {
                NO_USER
            }
            // Handles any other type of exception
            else -> {
                exception!!.stackTraceToString()
            }
        }
    }

    private fun errorLogin(str: String = "", error: String = "") {
        context.toast(error)
        _loginResult.postValue(false)

        var errMsg = "$str $LOGIN_FAIL "

        if (error != "") {
            errMsg += error
        }

        Timber.tag(TAG).e(errMsg)
    }

    private fun setLoggedInStatus(email: String, id: Long, username: String? = null) {
        context.setLoggedInStatus(true)
        context.setPreference("id", id)
        context.setPreference("email", email)
        if (username != null) {
            context.setPreference("username", username)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}