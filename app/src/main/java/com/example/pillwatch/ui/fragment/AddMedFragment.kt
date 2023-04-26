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
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.R
import com.example.pillwatch.data.datasource.local.AppDatabase
import com.example.pillwatch.databinding.FragmentAddMedBinding
import com.example.pillwatch.utils.AutoCompleteAdapter
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState
import com.example.pillwatch.viewmodel.AddMedViewModel
import com.example.pillwatch.viewmodel.factory.AddMedViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

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

        // NavController
        navController = NavHostFragment.findNavController(this)

        // ViewModel
        val application = requireNotNull(this.activity).application

        val medsDao = AppDatabase.getInstance(application).medsDao
        val userMedsDao = AppDatabase.getInstance(application).userMedsDao

        val viewModelFactory = AddMedViewModelFactory(medsDao, userMedsDao, application)
        viewModel = ViewModelProvider(this, viewModelFactory)[AddMedViewModel::class.java]
        binding.viewModel = viewModel

        binding.btnNext.setOnClickListener {
            lifecycleScope.launch {
                viewModel.addMedToUser(requireContext())
                viewModel.medDbSaveStatus.observe(viewLifecycleOwner, Observer {
                    if( it != null && it) {
                        viewModel.navigateAfterAdd(navController)
                    }
                })
            }
        }

        // Lifecycle
        binding.lifecycleOwner = this


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