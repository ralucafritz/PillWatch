package com.example.pillwatch.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.databinding.FragmentWelcomeBinding
import com.example.pillwatch.viewmodel.WelcomeViewModel

class WelcomeFragment: Fragment() {

    companion object {
        const val TAG = "WelcomeFragment"
    }

    private val welcomeViewModel: WelcomeViewModel by lazy {
        ViewModelProvider(this)[WelcomeViewModel::class.java]
    }

    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Binding
        binding = FragmentWelcomeBinding.inflate(inflater)

        val navController = NavHostFragment.findNavController(this)
        // Binding
        binding.welcomeViewModel = welcomeViewModel

        binding.lifecycleOwner = this

        binding.getStartedButton.setOnClickListener {
            welcomeViewModel.navigateToSignup(navController)
        }

        binding.logInWelcome.setOnClickListener {
            welcomeViewModel.navigateToLogin(navController)
        }

        return binding.root
    }
}