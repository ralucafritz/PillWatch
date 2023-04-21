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
import com.example.pillwatch.databinding.FragmentMedicationBinding
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState
import com.example.pillwatch.viewmodel.MedicationViewModel
import com.example.pillwatch.viewmodel.factory.MedicationViewModelFactory


class MedicationFragment : Fragment(){

    private lateinit var binding: FragmentMedicationBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: MedicationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentMedicationBinding.inflate(inflater)

        navBarVisibilityState(requireActivity(), R.id.medicationFragment)
        toolbarVisibilityState(requireActivity(), R.id.medicationFragment)

        // NavController
        navController = NavHostFragment.findNavController(this)

        // ViewModel
        val application = requireNotNull(this.activity).application

        val viewModelFactory = MedicationViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[MedicationViewModel::class.java]
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        return binding.root
    }
}