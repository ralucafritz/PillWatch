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
import com.example.pillwatch.databinding.FragmentProfileBinding
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState
import com.example.pillwatch.viewmodel.ProfileViewModel

class ProfileFragment : Fragment(){

    private lateinit var binding: FragmentProfileBinding
    private lateinit var navController: NavController
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this)[ProfileViewModel::class.java]}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentProfileBinding.inflate(inflater)

        navBarVisibilityState(requireActivity(), R.id.profileFragment)
        toolbarVisibilityState(requireActivity(), R.id.profileFragment)

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