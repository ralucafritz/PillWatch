package com.example.pillwatch.fragments.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.databinding.FragmentWelcomeBinding

class WelcomeFragment: Fragment() {
    private val welcomeViewModel: WelcomeViewModel by lazy {
        ViewModelProvider(this)[WelcomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Binding
        val binding = FragmentWelcomeBinding.inflate(inflater)

        // Binding
        binding.welcomeViewModel = welcomeViewModel

        binding.lifecycleOwner = this

        return binding.root
    }
}