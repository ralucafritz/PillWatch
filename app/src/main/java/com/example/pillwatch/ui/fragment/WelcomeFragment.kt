package com.example.pillwatch.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentWelcomeBinding
import com.example.pillwatch.ui.activity.LoginActivity
import com.example.pillwatch.ui.activity.SignupActivity
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.getLoggedInStatus
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState
import com.example.pillwatch.viewmodel.WelcomeViewModel

class WelcomeFragment: Fragment() {

    companion object {
        const val TAG = "WelcomeFragment"
    }

    private val viewModel: WelcomeViewModel by lazy {
        ViewModelProvider(this)[WelcomeViewModel::class.java]
    }

    private lateinit var binding: FragmentWelcomeBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentWelcomeBinding.inflate(inflater)

        navBarVisibilityState(requireActivity(), R.id.welcomeFragment)
        toolbarVisibilityState(requireActivity(), R.id.welcomeFragment)

        navController = NavHostFragment.findNavController(this)
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

        if(getLoggedInStatus()) {
            navController.popBackStack()
        }

        return binding.root
    }

    private val getResult = registerForActivityResult( ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            viewModel.navigateBack(navController)
        }
    }
}