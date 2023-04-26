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
import com.example.pillwatch.databinding.FragmentHomeBinding
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState
import com.example.pillwatch.viewmodel.HomeViewModel
import com.example.pillwatch.viewmodel.factory.HomeViewModelFactory

class HomeFragment : Fragment(){

    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentHomeBinding.inflate(inflater)

        navBarVisibilityState(requireActivity(), R.id.homeFragment)
        toolbarVisibilityState(requireActivity(), R.id.homeFragment)

        // NavController
        navController = NavHostFragment.findNavController(this)

        // ViewModel
        val application = requireNotNull(this.activity).application

        val viewModelFactory = HomeViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        binding.viewModel = viewModel

        binding.btnAdd.setOnClickListener {
            viewModel.navigateToAddAMed(navController)
        }

        // Lifecycle
        binding.lifecycleOwner = this

        return binding.root
    }

}