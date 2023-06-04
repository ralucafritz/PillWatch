package com.example.pillwatch.ui.signup

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.ActivitySignupBinding
import com.example.pillwatch.ui.login.LoginActivity
import com.example.pillwatch.ui.splash.SplashActivity
import com.example.pillwatch.ui.username.UsernameCreationFragment
import com.example.pillwatch.utils.extensions.ContextExtensions.isInternetConnected
import com.example.pillwatch.utils.extensions.ContextExtensions.toast
import com.example.pillwatch.utils.extensions.Extensions.timber
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import javax.inject.Inject

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    @Inject
    lateinit var viewModel: SignupViewModel

    lateinit var signupComponent: SignupComponent

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        signupComponent =
            (application as PillWatchApplication).appComponent.signupComponent().create()
        signupComponent.inject(this)
        super.onCreate(savedInstanceState)
        timber()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel

        viewModel.INVALID_EMAIL_ERR = resources.getString(R.string.invalid_email_err)
        viewModel.EMAIL_TAKEN_ERR = resources.getString(R.string.email_taken_err)
        viewModel.SHORT_PWD_ERR = resources.getString(R.string.short_pwd_err)
        viewModel.MATCHING_PWD_ERR = resources.getString(R.string.matching_pwd_err)
        viewModel.EMPTY_FIELDS_ERR = resources.getString(R.string.empty_fields_err)
        viewModel.SIGNUP_SUCCESS =  resources.getString(R.string.signup_success)
        viewModel.SIGNUP_FAIL = resources.getString(R.string.signup_fail)

        binding.lifecycleOwner = this

        // signup btn on click
        binding.buttonSignup.setOnClickListener {
            val email = binding.editTextEmailAddressSignup.text.toString()
            val password = binding.editTextPasswordSignup.text.toString()
            val confirmPassword = binding.editTextConfirmPasswordSignup.text.toString()
            val result = viewModel.isValid(email, password, confirmPassword)
            if (result.isValid) {
                viewModel.signup()
                viewModel.signupResult.observe(this) { res ->
                    if (res == true) {
                        if (viewModel.username != "") {
                            success()
                        } else {
                            supportFragmentManager.beginTransaction()
                                .add(R.id.fragment_holder, UsernameCreationFragment())
                                .commit()
                            binding.mainSignup.visibility = View.GONE
                        }
                    }
                }
            } else {
                toast(result.message)
            }
        }

        // login text on click
        binding.logIn.setOnClickListener {
            val intent = navigateToActivity(R.id.loginActivity)
            getResult.launch(intent)
        }

        // back button callback
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToActivity(R.id.welcomeFragment)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        // google sign in
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
                        viewModel.signupWithGoogle(it)
                        viewModel.signupResult.observe(this) { res ->
                            if (res == true) {
                                if (viewModel.username != "") {
                                    success()
                                } else {
                                    supportFragmentManager.beginTransaction()
                                        .add(
                                            R.id.fragment_holder,
                                            UsernameCreationFragment()
                                        )
                                        .commit()
                                    binding.mainSignup.visibility = View.GONE
                                }
                            } else {
                                toast("Google sign up failed")
                            }
                        }
                    }
                }
            }

        binding.btnGoogleSignUp.setOnClickListener {
            val signInIntent = gsc.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }

        viewModel.networkCheckStart.observe(this) {
            if (it != null && it) {
                viewModel.isInternetConnected(this.isInternetConnected())
            }
        }
    }

    // navigation function
    private fun navigateToActivity(activityId: Int): Intent {
        return Intent(
            this, when (activityId) {
                R.id.loginActivity -> {
                    LoginActivity::class.java
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