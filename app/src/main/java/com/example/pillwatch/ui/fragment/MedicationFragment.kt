package com.example.pillwatch.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillwatch.R
import com.example.pillwatch.data.datasource.local.AppDatabase
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.databinding.FragmentMedicationBinding
import com.example.pillwatch.utils.MedsListAdapter
import com.example.pillwatch.utils.extensions.ContextExtensions.toast
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState
import com.example.pillwatch.viewmodel.MedicationViewModel
import com.example.pillwatch.viewmodel.factory.MedicationViewModelFactory
import kotlinx.coroutines.launch


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

        val userMedsDao = AppDatabase.getInstance(application).userMedsDao

        val viewModelFactory = MedicationViewModelFactory(userMedsDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory)[MedicationViewModel::class.java]
        binding.viewModel = viewModel

        val recyclerView = binding.medsList

        recyclerView.layoutManager = LinearLayoutManager(context)
        // next button
        binding.btnAdd.setOnClickListener {
          viewModel.navigateToAddAMed(navController)
        }
        lifecycleScope .launch {
            val medsList = viewModel.getMedsList()
            if(medsList != null) {
                val adapter = MedsListAdapter(requireContext(), medsList)
                recyclerView.adapter = adapter
            }
        }

        // Lifecycle
        binding.lifecycleOwner = this

        return binding.root
    }
}