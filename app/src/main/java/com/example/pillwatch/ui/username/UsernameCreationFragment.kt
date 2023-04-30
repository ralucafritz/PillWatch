package com.example.pillwatch.ui.username

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.R
import com.example.pillwatch.data.source.local.AppDatabase
import com.example.pillwatch.databinding.FragmentUsernameCreationBinding
import com.example.pillwatch.utils.extensions.ContextExtensions.toast
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState

class UsernameCreationFragment : Fragment() {

    private lateinit var binding: FragmentUsernameCreationBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: UsernameCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Binding
        binding = FragmentUsernameCreationBinding.inflate(inflater)

        navBarVisibilityState(requireActivity(), R.id.usernameCreationFragment)
        toolbarVisibilityState(requireActivity(), R.id.usernameCreationFragment)

        // Create ViewModelFactory
        val application = requireNotNull(this.activity).application

        val userDao = AppDatabase.getInstance(application).userDao
        val viewModelFactory = UsernameCreationViewModelFactory(userDao, application)

        // ViewModel
        viewModel = ViewModelProvider(this, viewModelFactory)[UsernameCreationViewModel::class.java]

        // NavController
        navController = NavHostFragment.findNavController(this)

        // next button
        binding.buttonNext.setOnClickListener {
            val username = binding.usernameText.text .toString() ?: ""
            val validationResult = viewModel.isValid(username)
            if (validationResult.isValid) {
                viewModel.updateUsername()
                viewModel.navigateToHome(navController)
            } else {
                requireNotNull(this.activity).toast(validationResult.message)
            }
        }

        // Binding
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        return binding.root
    }

}