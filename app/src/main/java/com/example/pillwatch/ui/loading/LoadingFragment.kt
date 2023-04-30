package com.example.pillwatch.ui.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentLoadingBinding
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState

class LoadingFragment : Fragment(){

    private lateinit var binding: FragmentLoadingBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: LoadingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentLoadingBinding.inflate(inflater)

        navBarVisibilityState(requireActivity(), R.id.loadingFragment)
        toolbarVisibilityState(requireActivity(), R.id.loadingFragment)

        // NavController
        navController = NavHostFragment.findNavController(this)

        // ViewModel
        val application = requireNotNull(this.activity).application

        val viewModelFactory = LoadingViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoadingViewModel::class.java]
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        // Spinner, login status and navigation depending on login status
        binding.loadingSpinner.visibility = View.VISIBLE
        viewModel.checkLoginStatus()
        viewModel.loggedInStatus.observe(viewLifecycleOwner) {
            binding.loadingSpinner.visibility = View.GONE
            if(it) {
                when (viewModel.username.value) {
                    "" -> viewModel.navigateToUsernameCreate(navController)
                    else -> viewModel.navigateToHome(navController)
                }
            } else {
                viewModel.navigateToWelcome(navController)
            }
        }

        return binding.root
    }
}