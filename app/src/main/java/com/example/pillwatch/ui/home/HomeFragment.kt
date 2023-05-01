package com.example.pillwatch.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentHomeBinding
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarBottomNavVisibility
import javax.inject.Inject

class HomeFragment : Fragment(){

    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var viewModel: HomeViewModel

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
        binding = FragmentHomeBinding.inflate(inflater)

        toolbarBottomNavVisibility(requireActivity(), R.id.homeFragment)

        // ViewModel
        binding.viewModel = viewModel

        binding.btnAdd.setOnClickListener {
            this@HomeFragment.findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToAddMedFragment()
            )
        }

        // Lifecycle
        binding.lifecycleOwner = this

        return binding.root
    }
}