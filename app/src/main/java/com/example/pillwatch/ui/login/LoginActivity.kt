package com.example.pillwatch.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.ActivityLoginBinding
import com.example.pillwatch.ui.signup.SignupActivity
import com.example.pillwatch.ui.splash.SplashActivity
import com.example.pillwatch.ui.username.UsernameCreationFragment
import com.example.pillwatch.utils.extensions.ContextExtensions.isInternetConnected
import com.example.pillwatch.utils.extensions.ContextExtensions.toast
import com.example.pillwatch.utils.extensions.Extensions.timber
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var viewModel: LoginViewModel

    lateinit var loginComponent: LoginComponent

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        loginComponent =
            (application as PillWatchApplication).appComponent.loginComponent().create()
        loginComponent.inject(this)
        super.onCreate(savedInstanceState)
        timber()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        // login btn on click
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmailAddressLogin.text.toString()
            val password = binding.editTextPasswordLogin.text.toString()
            val result = viewModel.isValid(email, password)
            if (result.isValid) {
                viewModel.login()
                viewModel.loginResult.observe(this) { res ->
                    if (res == true) {
                        if (viewModel.username != "") {
                            success()
                        } else {
                            supportFragmentManager.beginTransaction()
                                .add(R.id.fragment_holder, UsernameCreationFragment())
                                .commit()
                            binding.mainLogin.visibility = View.GONE
                        }
                    }
                }
            } else {
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
                navigateToActivity(R.id.welcomeFragment)
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
                                if (viewModel.username != "") {
                                    success()
                                } else {
                                    supportFragmentManager.beginTransaction()
                                        .add(R.id.fragment_holder, UsernameCreationFragment())
                                        .commit()
                                    binding.mainLogin.visibility = View.INVISIBLE
                                }
                            } else {
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

        viewModel.networkCheckStart.observe(this) {
            if(it != null && it) {
                viewModel.isInternetConnected(this.isInternetConnected())
            }
        }
    }

    private fun navigateToActivity(activityId: Int): Intent {
        return Intent(
            this, when (activityId) {
                R.id.signupActivity -> {
                    SignupActivity::class.java
                }
                else -> {
                    SplashActivity::class.java
                }
            }
        )
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                success()
            }
        }

    fun success() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}

