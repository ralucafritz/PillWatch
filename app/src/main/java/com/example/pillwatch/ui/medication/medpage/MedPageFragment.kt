package com.example.pillwatch.ui.medication.medpage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentMedPageBinding
import com.example.pillwatch.ui.alarms.AlarmsPerDayFragmentArgs
import com.example.pillwatch.ui.main.MainActivity
import kotlinx.coroutines.launch
import javax.inject.Inject

class MedPageFragment : Fragment() {
    private lateinit var binding: FragmentMedPageBinding

    @Inject
    lateinit var viewModel: MedPageViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // inject the fragment with the app component, allowing it to access the ViewModel
        (requireActivity().application as PillWatchApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentMedPageBinding.inflate(inflater)

        (requireActivity() as MainActivity).medPageToolbar(true, R.id.medPageFragment)

        val id = AlarmsPerDayFragmentArgs.fromBundle(requireArguments()).id

        // Lifecycle
        binding.lifecycleOwner = this

        // ViewModel
        binding.viewModel = viewModel

        viewModel.getMedEntity(id)

        viewModel.medEntity.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.medName.text = it.tradeName
                if (it.medId != null) {
                    binding.medItemFab.setImageResource(R.drawable.ic_check)
                    binding.medItemFab.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        )
                    )
                } else {
                    binding.medItemFab.setImageResource(R.drawable.ic_close)
                    binding.medItemFab.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                }
            }
        }

        return binding.root
    }
}