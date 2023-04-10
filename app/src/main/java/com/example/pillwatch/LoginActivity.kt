package com.example.pillwatch

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.pillwatch.databinding.ActivityLoginBinding
import com.example.pillwatch.extensions.Extensions.timber
import com.example.pillwatch.extensions.Extensions.toast

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timber()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        binding.editTextEmailAddressLogin.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.setEmail(p0.toString())
            }
        })

        binding.editTextPasswordLogin.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.setPassword(p0.toString())
            }
        })

        binding.buttonLogin.setOnClickListener {
            val result = viewModel.isValid()
            if(result.isValid) {
                viewModel.login().observe(this) { res ->
                    if (res == true) {
                        navigateToActivity(R.id.nav_host_fragment)
                    }
                }
            }else {
                toast(result.message)
            }
        }

        binding.signup.setOnClickListener {
            navigateToActivity(R.id.signupActivity)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToActivity(R.id.nav_host_fragment)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun navigateToActivity(activityId: Int) {

        val intent = Intent(this, when (activityId) {
            R.id.nav_host_fragment -> { MainActivity::class.java }
            R.id.signupActivity-> { SignupActivity::class.java }
            else -> { MainActivity::class.java }
        })

        startActivity(intent)
    }
}