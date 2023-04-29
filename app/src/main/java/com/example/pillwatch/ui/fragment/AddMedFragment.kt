package com.example.pillwatch.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.pillwatch.R
import com.example.pillwatch.data.datasource.local.AppDatabase
import com.example.pillwatch.databinding.FragmentAddMedBinding
import com.example.pillwatch.utils.AutoCompleteAdapter
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState
import com.example.pillwatch.viewmodel.AddMedViewModel
import com.example.pillwatch.viewmodel.factory.AddMedViewModelFactory
import kotlinx.coroutines.launch


class AddMedFragment : Fragment() {

    private lateinit var binding: FragmentAddMedBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: AddMedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentAddMedBinding.inflate(inflater)

        navBarVisibilityState(requireActivity(), R.id.addMedFragment)
        toolbarVisibilityState(requireActivity(), R.id.addMedFragment)

        // ViewModel
        val application = requireNotNull(this.activity).application

        val medsDao = AppDatabase.getInstance(application).medsDao
        val userMedsDao = AppDatabase.getInstance(application).userMedsDao

        val viewModelFactory = AddMedViewModelFactory(medsDao, userMedsDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory)[AddMedViewModel::class.java]
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        binding.btnNext.setOnClickListener {
            lifecycleScope.launch {
                viewModel.addMedToUser(requireContext())
            }
        }

        viewModel.navigationCheck.observe(viewLifecycleOwner, Observer {
            if( it == true) {
                this.findNavController().navigate(AddMedFragmentDirections.actionAddMedFragmentToAlarmFrequencyFragment(
                    viewModel.medAddedId.value!!
                ))
                viewModel.navigationCompleteToAlarmFrequency()
            }
        })

        viewModel.medName.observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                if (viewModel.medName.value != null) {
                    viewModel.searchMedName(it)
                    val adapter =  AutoCompleteAdapter(requireContext(),  Pair(viewModel.nameList ?: listOf<String>(), viewModel.concentrationList  ?: listOf<String>()))
                    binding.medName.setAdapter(adapter)
                }
            }
        })

        binding.medName.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                viewModel.getPairAtPosition(position)
            }

        return binding.root
    }

}