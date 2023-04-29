package com.example.pillwatch.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentAlarmsPerDayBinding
import com.example.pillwatch.databinding.FragmentUserMedBinding
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState
import com.example.pillwatch.viewmodel.AlarmsPerDayViewModel
import com.example.pillwatch.viewmodel.UserMedViewModel

class UserMedFragment: Fragment() {
        private lateinit var binding: FragmentUserMedBinding
        private lateinit var navController: NavController
        private val viewModel: UserMedViewModel by lazy {
            ViewModelProvider(this)[UserMedViewModel::class.java]}

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            // Binding
            binding = FragmentUserMedBinding.inflate(inflater)

            navBarVisibilityState(requireActivity(), R.id.userMedFragment)
            toolbarVisibilityState(requireActivity(), R.id.userMedFragment)

            // NavController
            navController = NavHostFragment.findNavController(this)

            // ViewModel
            val application = requireNotNull(this.activity).application

            binding.viewModel = viewModel

            // Lifecycle
            binding.lifecycleOwner = this

            return binding.root
        }
}