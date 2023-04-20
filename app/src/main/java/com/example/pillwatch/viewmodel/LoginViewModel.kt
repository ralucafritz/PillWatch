package com.example.pillwatch.viewmodel

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pillwatch.data.datasource.local.UserDao
import com.example.pillwatch.data.model.UserEntity
import com.example.pillwatch.data.repository.UserRepository
import com.example.pillwatch.utils.AuthResultProperty
import com.example.pillwatch.utils.FirebaseUtils.firebaseAuth
import com.example.pillwatch.utils.FirebaseUtils.firebaseUser
import com.example.pillwatch.utils.Role
import com.example.pillwatch.utils.ValidationProperty
import com.example.pillwatch.utils.checkPassword
import com.example.pillwatch.utils.extensions.ContextExtensions.setLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.setPreference
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginViewModel(userDao: UserDao, application: Application) :
    AndroidViewModel(application) {

    private val context: Context by lazy { application.applicationContext }

    private val repository = UserRepository(userDao)

    // start the job
    private var viewModelJob = Job()

    // coroutine scope for main thread
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // coroutine scope for database stuff
    private val dbCoroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)


    companion object {
        const val TAG = "Auth"
        const val INVALID_EMAIL_ERR = "Invalid email."
        const val SHORT_PSW_ERR = "Password is too short. Minimum 8 characters."
        const val EMPTY_FIELDS_ERR = "Please fill all the fields."
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

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun isValid(): ValidationProperty {
        return when {
            _email.value == null || _password.value == null -> ValidationProperty(
                false,
                EMPTY_FIELDS_ERR
            )

            !Patterns.EMAIL_ADDRESS.matcher(_email.value!!).matches() -> ValidationProperty(
                false,
                INVALID_EMAIL_ERR
            )

            _password.value!!.length < 8 -> ValidationProperty(false, SHORT_PSW_ERR)
            else -> ValidationProperty(true, "")
        }
    }

    fun login() {
        coroutineScope.launch {
            val email = _email.value!!
            val password = _password.value!!
            val user = withContext(Dispatchers.IO) { repository.getUserByEmail(email) }
            if (user.value != null && checkPassword(password, user.value!!.password)) {
                Timber.tag(TAG).d("Login successful: $firebaseUser")
                setLoggedInStatus(user.value!!.email, user.value!!.id)
                _loginResult.postValue(true)
            } else {
                _loginResult.postValue(false)
                Timber.tag(TAG).d("Login failed.")
            }
        }
    }

    fun loginWithGoogle(signInAccountTask: Task<GoogleSignInAccount>) {
        coroutineScope.launch {
            try {
                val account = signInAccountTask.getResult(ApiException::class.java)
                val res = insertWithGoogle(account.idToken ?: "")
                _loginResult.postValue(res)
            } catch (err: ApiException) {
                _loginResult.postValue(false)
                Timber.tag(TAG).w("Google Sign in Failure: $err")
            }
        }
    }

    private suspend fun insertWithGoogle(idToken: String): Boolean {
        return withContext(Dispatchers.Main + viewModelJob) {
            var result: Boolean
            if (idToken == "") {
                result = false
            } else {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = signInWithCredential(credential, idToken)
                result = if (authResult.success && authResult.user != null) {
                    withContext(Dispatchers.IO) {
                        val loadedUser = repository.getUserByIdToken(idToken)
                        var idToSet: Long?
                        val emailToSet = if (loadedUser.value == null) {
                            val newUser = repository.insert(authResult.user!!)
                            idToSet = repository.getIdByEmail(newUser.email)
                            newUser.email
                        } else {
                            idToSet = loadedUser.value!!.id
                            loadedUser.value!!.email
                        }
                        setLoggedInStatus(emailToSet, idToSet!!)
                        true
                    }
                } else {
                    context.setLoggedInStatus(false)
                    false
                }
            }
            result
        }
    }

    private suspend fun signInWithCredential(
        credential: AuthCredential,
        idToken: String
    ): AuthResultProperty {
        return suspendCoroutine { continuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val email = task.result.user?.email!!
                        val password = UUID.randomUUID().toString()
                        val user = UserEntity(
                            0L,
                            email = email,
                            null,
                            password,
                            idToken,
                            Role.USER
                        )
                        continuation.resume(AuthResultProperty(true, task.result, user))
                    } else {
                        continuation.resume(AuthResultProperty(false, null, null))
                    }
                }
        }
    }

    private fun setLoggedInStatus(email: String, id: Long) {
        context.setLoggedInStatus(true)
        context.setPreference("id", id)
        context.setPreference("email", email)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}