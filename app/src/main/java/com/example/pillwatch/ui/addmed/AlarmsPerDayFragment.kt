package com.example.pillwatch.ui.addmed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentAlarmsPerDayBinding
import com.example.pillwatch.utils.AlarmTiming
import com.example.pillwatch.utils.extensions.FragmentExtensions.navBarVisibilityState
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarVisibilityState
import timber.log.Timber

class AlarmsPerDayFragment: Fragment() {
    private lateinit var binding: FragmentAlarmsPerDayBinding
    private lateinit var navController: NavController
    private val viewModel: AlarmsPerDayViewModel by lazy {
        ViewModelProvider(this)[AlarmsPerDayViewModel::class.java]}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentAlarmsPerDayBinding.inflate(inflater)

        navBarVisibilityState(requireActivity(), R.id.alarmsPerDayFragment)
        toolbarVisibilityState(requireActivity(), R.id.alarmsPerDayFragment)

        val medId = AlarmsPerDayFragmentArgs.fromBundle(requireArguments()).id
        val alarmTiming = AlarmsPerDayFragmentArgs.fromBundle(requireArguments()).alarmTiming

        // NavController
        navController = NavHostFragment.findNavController(this)

        binding.slider.isVisible = false
        if(alarmTiming == AlarmTiming.EVERY_X_HOURS) {
            binding.slider.isVisible = true
        }

        binding.slider.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.everyXHours = progress
                val newText = "Every ${progress+1} hours"
                binding.valueText.text = newText
                Timber.tag("alarm").d(viewModel.everyXHours.toString())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        // ViewModel
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        return binding.root
    }

}