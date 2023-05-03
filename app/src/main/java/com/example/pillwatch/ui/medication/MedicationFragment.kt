package com.example.pillwatch.ui.medication

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
import com.example.pillwatch.databinding.FragmentMedicationBinding
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarBottomNavVisibility
import kotlinx.coroutines.launch
import javax.inject.Inject


class MedicationFragment : Fragment() {

    private lateinit var binding: FragmentMedicationBinding

    @Inject
    lateinit var viewModel: MedicationViewModel

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
        binding = FragmentMedicationBinding.inflate(inflater)
        toolbarBottomNavVisibility(requireActivity(), R.id.medicationFragment)

        // ViewModel
        binding.viewModel = viewModel

        val recyclerView = binding.medsList

        recyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            val medsList = viewModel.getMedsList()
            if (medsList != null) {
                val adapter = MedsListAdapter(requireContext(), medsList)
                recyclerView.adapter = adapter
            }
        }

        // next button
        binding.btnAdd.setOnClickListener {
            this@MedicationFragment.findNavController().navigate(
                MedicationFragmentDirections.actionMedicationFragmentToAddMedFragment()
            )
        }

        // Lifecycle
        binding.lifecycleOwner = this

        return binding.root
    }
}