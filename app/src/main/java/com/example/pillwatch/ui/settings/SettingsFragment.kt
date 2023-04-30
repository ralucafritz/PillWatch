package com.example.pillwatch.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentSettingsBinding
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState

class SettingsFragment : Fragment(){

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentSettingsBinding.inflate(inflater)

        navBarVisibilityState(requireActivity(), R.id.settingsFragment)
        toolbarVisibilityState(requireActivity(), R.id.settingsFragment)

        // NavController
        navController = NavHostFragment.findNavController(this)

        // ViewModel
        val application = requireNotNull(this.activity).application

        val viewModelFactory = SettingsViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        return binding.root
    }
}