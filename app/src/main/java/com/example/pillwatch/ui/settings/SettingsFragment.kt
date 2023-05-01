package com.example.pillwatch.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentSettingsBinding
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarBottomNavVisibility
import javax.inject.Inject

class SettingsFragment : Fragment(){

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var viewModel: SettingsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as PillWatchApplication).appComponent.userManager().userComponent!!.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentSettingsBinding.inflate(inflater)

        toolbarBottomNavVisibility(requireActivity(), R.id.settingsFragment)

        // NavController
        navController = NavHostFragment.findNavController(this)

        // ViewModel
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        return binding.root
    }
}