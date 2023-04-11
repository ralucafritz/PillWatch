package com.example.pillwatch.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.R
import com.example.pillwatch.data.datasource.local.AppDatabase
import com.example.pillwatch.data.datasource.local.UserDao
import com.example.pillwatch.databinding.ActivitySignupBinding
import com.example.pillwatch.utils.extensions.Extensions.timber
import com.example.pillwatch.utils.extensions.Extensions.toast
import com.example.pillwatch.viewmodel.SignupViewModel
import com.example.pillwatch.viewmodel.factory.SignupViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.coroutineScope

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: SignupViewModel

    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timber()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userDao = AppDatabase.getInstance(application).userDao

        val viewModelFactory = SignupViewModelFactory(userDao, application)
         viewModel = ViewModelProvider(this, viewModelFactory)[SignupViewModel::class.java]

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        // get data from email EditText
        binding.editTextEmailAddressSignup.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                viewModel.setEmail(p0.toString())
            }
        })
        // get data from pwd EditText
        binding.editTextPasswordSignup.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                viewModel.setPassword(p0.toString())
            }
        })

        // get data from confirm pwd EditText
        binding.editTextConfirmPasswordSignup.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                viewModel.setConfirmedPassword(p0.toString())
            }
        })

        // signup btn on click
        binding.buttonSignup.setOnClickListener {
            val result = viewModel.isValid()
            if (result.isValid) {
                viewModel.signup()
                viewModel.signupResult.observe(this) { res ->
                    if (res == true) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            } else {
                toast(result.message)
            }
        }

        // login text on click
        binding.logIn.setOnClickListener {
            val intent = navigateToActivity(R.id.loginActivity)
            startActivity(intent)
            finish()
        }

        // back button callback
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToActivity(R.id.nav_host_fragment)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        // google signin
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        val googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    task.addOnCompleteListener { it
                    viewModel.signupWithGoogle(it)
                    viewModel.signupResult.observe(this) { res ->
                        if (res == true) {
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            toast("Google sign up failed")
                        }
                    }
                }
            }
    }

    binding.btnGoogleSignUp.setOnClickListener  {
        val signInIntent = gsc.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }
}

// navigation function
private fun navigateToActivity(activityId: Int): Intent {
    return Intent(
        this, when (activityId) {
            R.id.nav_host_fragment -> {
                MainActivity::class.java
            }
            R.id.loginActivity -> {
                LoginActivity::class.java
            }
            else -> {
                MainActivity::class.java
            }
        }
    )
}
}