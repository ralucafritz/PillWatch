package com.example.pillwatch.ui.addmed

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentAddMedBinding
import com.example.pillwatch.utils.AutoCompleteAdapter
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarBottomNavVisibility
import kotlinx.coroutines.launch
import javax.inject.Inject


class AddMedFragment : Fragment() {

    private lateinit var binding: FragmentAddMedBinding

    @Inject
    lateinit var viewModel: AddMedViewModel

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
        binding = FragmentAddMedBinding.inflate(inflater)

        toolbarBottomNavVisibility(requireActivity(), R.id.addMedFragment)

        // ViewModel
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        binding.btnNext.setOnClickListener {
            lifecycleScope.launch {
                viewModel.addMedToUser()
                viewModel.navigationCheck.observe(viewLifecycleOwner) {
                if(viewModel.navigationCheck.value != null && viewModel.navigationCheck.value!!) {
                    this@AddMedFragment.findNavController().navigate(
                        AddMedFragmentDirections.actionAddMedFragmentToAlarmFrequencyFragment(
                        viewModel.medAddedId.value!!
                    ))
                    viewModel.navigationCompleteToAlarmFrequency()
                }
            }
            }
        }

        viewModel.medName.observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                if (viewModel.medName.value != null) {
                    viewModel.searchMedName(it)
                    val adapter =  AutoCompleteAdapter(requireContext(),  Pair(viewModel.nameList, viewModel.concentrationList))
                    binding.medName.setAdapter(adapter)
                }
            }
        })

        binding.medName.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.getPairAtPosition(position)
            }

        viewModel.isAlertNeeded.observe(viewLifecycleOwner) {
            if(it != null && it) {
                viewModel.isAlertNeeded(requireContext())
            }
        }

        return binding.root
    }

}