package com.example.pillwatch.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.databinding.FragmentLoadingBinding
import com.example.pillwatch.viewmodel.LoadingViewModel
import com.example.pillwatch.viewmodel.factory.LoadingViewModelFactory

class LoadingFragment : Fragment(){

    private lateinit var binding: FragmentLoadingBinding
    private lateinit var navController: NavController
    private lateinit var loadingViewModel: LoadingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentLoadingBinding.inflate(inflater)

        // NavController
        navController = NavHostFragment.findNavController(this)

        // ViewModel
        val application = requireNotNull(this.activity).application

        val viewModelFactory = LoadingViewModelFactory(application)
        loadingViewModel = ViewModelProvider(this, viewModelFactory)[LoadingViewModel::class.java]
        binding.loadingViewModel = loadingViewModel

        // Lifecycle
        binding.lifecycleOwner = this

        // Spinner, login status and navigation depending on login status
        binding.loadingSpinner.visibility = View.VISIBLE
        loadingViewModel.checkLoginStatus()
        loadingViewModel.loggedInStatus.observe(viewLifecycleOwner) {
            binding.loadingSpinner.visibility = View.GONE
            if(it) {
                loadingViewModel.navigateToHome(navController)
            } else {
                loadingViewModel.navigateToWelcome(navController)
            }
        }

        return binding.root
    }
}