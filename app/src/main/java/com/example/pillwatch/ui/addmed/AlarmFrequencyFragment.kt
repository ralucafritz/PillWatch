package com.example.pillwatch.ui.addmed

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentAlarmFrequencyBinding
import com.example.pillwatch.utils.AlarmTiming
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarBottomNavVisibility

class AlarmFrequencyFragment: Fragment() {

    private lateinit var binding: FragmentAlarmFrequencyBinding
    private lateinit var navController: NavController
    private val viewModel: AlarmFrequencyViewModel by lazy {
        ViewModelProvider(this)[AlarmFrequencyViewModel::class.java]}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentAlarmFrequencyBinding.inflate(inflater)

        toolbarBottomNavVisibility(requireActivity(), R.id.alarmFrequencyFragment)

        // ViewModel
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        val medId = AlarmFrequencyFragmentArgs.fromBundle(requireArguments()).id

        for(enumValue in AlarmTiming.values()) {
            val radioButton = RadioButton(requireContext())
            radioButton.text = enumValue.label
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
        }

        binding.buttonNext.setOnClickListener {
            if (medId != -1L && viewModel.selectedOption.value!= null ) {
                if (viewModel.selectedOption.value != AlarmTiming.NO_REMINDERS) {
                    this.findNavController().navigate(AlarmFrequencyFragmentDirections.actionAlarmFrequencyFragmentToAlarmsPerDayFragment(medId,
                        viewModel.selectedOption.value!!
                    )
                    )
                } else {
                    this.findNavController().navigate(AlarmFrequencyFragmentDirections.actionAlarmFrequencyFragmentToMedicationFragment())
                }
            }
        }

        return binding.root
    }

}