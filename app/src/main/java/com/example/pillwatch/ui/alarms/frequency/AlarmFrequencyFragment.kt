package com.example.pillwatch.ui.alarms.frequency

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentAlarmFrequencyBinding
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.utils.AlarmTiming
import timber.log.Timber

class AlarmFrequencyFragment : Fragment() {

    private lateinit var binding: FragmentAlarmFrequencyBinding

    private val viewModel: AlarmFrequencyViewModel by lazy {
        ViewModelProvider(this)[AlarmFrequencyViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentAlarmFrequencyBinding.inflate(inflater)

        (requireActivity() as MainActivity).navBarToolbarBottomNav(
            false,
            R.id.alarmFrequencyFragment
        )

        // ViewModel
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        val medId = AlarmFrequencyFragmentArgs.fromBundle(requireArguments()).id

        setupRadioGroup()

        binding.buttonNext.setOnClickListener {
            if (medId != "" && viewModel.selectedOption.value != null) {
                try {
                    if (viewModel.selectedOption.value != AlarmTiming.NO_REMINDERS) {
                        this.findNavController().navigate(
                            AlarmFrequencyFragmentDirections.actionAlarmFrequencyFragmentToAlarmsPerDayFragment(
                                medId,
                                viewModel.selectedOption.value!!
                            )
                        )
                    } else {
                        this.findNavController()
                            .navigate(AlarmFrequencyFragmentDirections.actionAlarmFrequencyFragmentToMedicationFragment())
                    }
                } catch (e: Exception) {
                    Timber.tag("AlarmFrequency").e("Error found for AlarmFrequencyNavigation: $e")
                }
            }
        }

        return binding.root
    }

    private fun setupRadioGroup() {
        val defaultOption = AlarmTiming.EVERY_X_HOURS
        for (enumValue in AlarmTiming.values()) {
            val radioButton = RadioButton(requireContext())
            radioButton.text = getString(enumValue.labelResId)
            radioButton.tag = enumValue
            radioButton.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    viewModel.selectedOption.value = buttonView.tag as AlarmTiming
                }
            }
            val layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 0, 0, 20)
            radioButton.layoutParams = layoutParams
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            binding.radioGroup.addView(radioButton)
            if (enumValue == defaultOption) {
                radioButton.isChecked = true
            }
        }
        viewModel.selectedOption.value = defaultOption
    }
}

