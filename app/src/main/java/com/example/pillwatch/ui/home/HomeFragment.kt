package com.example.pillwatch.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentHomeBinding
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.ui.medication.MedicationFragmentDirections
import com.example.pillwatch.ui.medication.MedsListAdapter
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var viewModel: HomeViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as PillWatchApplication).appComponent.userManager().userComponent!!.inject(
            this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentHomeBinding.inflate(inflater)

        (requireActivity() as MainActivity).navBarToolbarBottomNav(true, R.id.homeFragment)

        // ViewModel
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        val recyclerView = binding.homeList
        recyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            val medsList = viewModel.getMedsList()
            if (medsList != null) {
                val adapter = HomeListAdapter(medsList)
                recyclerView.adapter = adapter

                adapter.onItemClick = {
                    this@HomeFragment.findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToMedPageFragment(it)
                    )
                }
            }
        }

        binding.btnAdd.setOnClickListener {
            this@HomeFragment.findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToAddMedFragment()
            )
        }


        return binding.root
    }
}