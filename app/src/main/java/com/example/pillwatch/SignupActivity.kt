package com.example.pillwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.pillwatch.databinding.ActivitySignupBinding
import com.example.pillwatch.extensions.Extensions.timber
import com.example.pillwatch.extensions.Extensions.toast

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timber()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SignupViewModel::class.java]

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        binding.editTextEmailAddressSignup.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.setEmail(p0.toString())
            }
        })

        binding.editTextPasswordSignup.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.setPassword(p0.toString())
            }
        })

        binding.editTextConfirmPasswordSignup.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.setConfirmedPassword(p0.toString())
            }
        })

        binding.buttonSignup.setOnClickListener {
            val result = viewModel.isValid()
            if(result.isValid) {
                viewModel.signup().observe(this) { res ->
                    if (res == true) {
                        navigateToActivity(R.id.nav_host_fragment)
                    }
                }
            }else {
                toast(result.message)
            }
        }

        binding.logIn.setOnClickListener{
            navigateToActivity(R.id.loginActivity)
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
            R.id.loginActivity-> { LoginActivity::class.java }
            else -> { MainActivity::class.java }
        })

        startActivity(intent)
    }

}