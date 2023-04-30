package com.example.pillwatch.ui.medication.medpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentMedPageBinding
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState

class MedPageFragment: Fragment() {
        private lateinit var binding: FragmentMedPageBinding
        private lateinit var navController: NavController
        private val viewModel: MedPageViewModel by lazy {
            ViewModelProvider(this)[MedPageViewModel::class.java]}

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            // Binding
            binding = FragmentMedPageBinding.inflate(inflater)

            navBarVisibilityState(requireActivity(), R.id.userMedFragment)
            toolbarVisibilityState(requireActivity(), R.id.userMedFragment)

            // NavController
            navController = NavHostFragment.findNavController(this)

            // ViewModel
            binding.viewModel = viewModel

            // Lifecycle
            binding.lifecycleOwner = this

            return binding.root
        }
}