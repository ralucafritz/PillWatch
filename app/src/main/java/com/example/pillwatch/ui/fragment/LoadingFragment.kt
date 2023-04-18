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
import com.example.pillwatch.databinding.FragmentLoadingBinding
import com.example.pillwatch.utils.extensions.FragmentExtensions.getLoggedInStatus
import com.example.pillwatch.viewmodel.LoadingViewModel
import com.example.pillwatch.viewmodel.LoginViewModel
import com.example.pillwatch.viewmodel.factory.LoadingViewModelFactory
import com.example.pillwatch.viewmodel.factory.LoginViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadingFragment : Fragment(){

    private lateinit var binding: FragmentLoadingBinding
    private lateinit var navController: NavController
    private lateinit var loadingViewModel: LoadingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Binding
        binding = FragmentLoadingBinding.inflate(inflater)
        navController = NavHostFragment.findNavController(this)
        // ViewModel

        val application = requireNotNull(this.activity).application

        val viewModelFactory = LoadingViewModelFactory(application)
        loadingViewModel = ViewModelProvider(this, viewModelFactory)[LoadingViewModel::class.java]
        binding.loadingViewModel = loadingViewModel

        binding.lifecycleOwner = this


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!::binding.isInitialized) {
            // Return early if binding has not been initialized yet
            return
        }

    }

    private fun loadDataInBackground() {

        val isLoggedIn = getLoggedInStatus()

        navController = NavHostFragment.findNavController(this)

        if(isLoggedIn) {
            loadingViewModel.navigateToHome(navController)
        } else {
            loadingViewModel.navigateToWelcome(navController)
        }
    }
}