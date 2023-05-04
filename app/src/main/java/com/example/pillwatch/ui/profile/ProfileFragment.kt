package com.example.pillwatch.ui.profile

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
import com.example.pillwatch.ui.main.MainActivity

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

        (requireActivity() as MainActivity).navBarToolbarBottomNav( true, R.id.profileFragment)

        // NavController
        navController = NavHostFragment.findNavController(this)

        // ViewModel
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        return binding.root
    }

}