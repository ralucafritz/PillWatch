package com.example.pillwatch.ui.welcome

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.databinding.FragmentWelcomeBinding
import com.example.pillwatch.ui.login.LoginActivity
import com.example.pillwatch.ui.signup.SignupActivity
import com.example.pillwatch.ui.splash.SplashActivity

class WelcomeFragment: Fragment() {

    private val viewModel: WelcomeViewModel by lazy {
        ViewModelProvider(this)[WelcomeViewModel::class.java]}

    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentWelcomeBinding.inflate(inflater)

        // Binding
        binding.welcomeViewModel = viewModel

        binding.lifecycleOwner = this

        binding.getStartedButton.setOnClickListener {
            val intent = Intent(activity, SignupActivity::class.java)
            getResult.launch(intent)
        }

        binding.logInWelcome.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            getResult.launch(intent)
        }
        return binding.root
    }

    private val getResult = registerForActivityResult( ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            (activity as SplashActivity).checkLoggedIn()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

}