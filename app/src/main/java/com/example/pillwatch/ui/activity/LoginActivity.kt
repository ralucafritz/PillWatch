package com.example.pillwatch.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.R
import com.example.pillwatch.data.datasource.local.AppDatabase
import com.example.pillwatch.databinding.ActivityLoginBinding
import com.example.pillwatch.utils.extensions.ContextExtensions.setLoggedInStatus
import com.example.pillwatch.utils.extensions.ContextExtensions.toast
import com.example.pillwatch.utils.extensions.Extensions.timber
import com.example.pillwatch.viewmodel.LoginViewModel
import com.example.pillwatch.viewmodel.factory.LoginViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timber()
        setTheme(R.style.AppTheme)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userDao = AppDatabase.getInstance(application).userDao

        val viewModelFactory = LoginViewModelFactory(userDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        // login btn on click
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmailAddressLogin.text .toString() ?: ""
            val password = binding.editTextPasswordLogin.text .toString() ?: ""
            val result = viewModel.isValid(email, password)
            if (result.isValid) {
                viewModel.login()
                viewModel.loginResult.observe(this) { res ->
                    if (res == true) {
                        success()
                    }
                }
            } else {
                setLoggedInStatus(false)
                toast(result.message)
            }
        }
        // signup txt on click
        binding.signup.setOnClickListener {
            val intent = navigateToActivity(R.id.signupActivity)
            getResult.launch(intent)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToActivity(R.id.nav_host_fragment)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        // google login
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        val googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    task.addOnCompleteListener {
                        viewModel.loginWithGoogle(it)
                        viewModel.loginResult.observe(this) { res ->
                            if (res == true) {
                                success()
                            } else {
                                setLoggedInStatus(false)
                                toast("Google sign in failed")
                            }
                        }
                    }
                }
            }

        binding.btnGoogleSignIn.setOnClickListener {
            val signInIntent = gsc.signInIntent
            googleSignInLauncher.launch(signInIntent)

        }
    }

    private fun navigateToActivity(activityId: Int): Intent {
        return Intent(
            this, when (activityId) {
                R.id.nav_host_fragment -> {
                    MainActivity::class.java
                }
                R.id.signupActivity -> {
                    SignupActivity::class.java
                }
                else -> {
                    MainActivity::class.java
                }
            }
        )
    }

    private val getResult = registerForActivityResult( ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            success()
        }
    }

    private fun success() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}

