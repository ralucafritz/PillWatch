package com.example.pillwatch.ui.addmed

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.databinding.FragmentAlarmsPerDayBinding
import com.example.pillwatch.ui.login.LoginActivity
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.ui.medication.MedsListAdapter
import com.example.pillwatch.ui.signup.SignupActivity
import com.example.pillwatch.utils.AlarmTiming
import com.example.pillwatch.utils.extensions.FragmentExtensions.toolbarBottomNavVisibility
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AlarmsPerDayFragment: Fragment(), OnAlarmUpdatedListener {
    private lateinit var binding: FragmentAlarmsPerDayBinding

    @Inject
    lateinit var viewModel: AlarmsPerDayViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as PillWatchApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentAlarmsPerDayBinding.inflate(inflater)

        toolbarBottomNavVisibility(requireActivity(), R.id.alarmsPerDayFragment)

        val medId = AlarmsPerDayFragmentArgs.fromBundle(requireArguments()).id
        val alarmTiming = AlarmsPerDayFragmentArgs.fromBundle(requireArguments()).alarmTiming

        // ViewModel
        binding.viewModel = viewModel

        binding.slider.isVisible = false
        if(alarmTiming == AlarmTiming.EVERY_X_HOURS) {
            binding.slider.isVisible = true
            binding.slider.progress = 12
            changeValueText( binding.slider.progress)
        }

        binding.slider.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.everyXHours = progress
                changeValueText(progress)
                Timber.tag("alarm").d(viewModel.everyXHours.toString())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        val recyclerView = binding.alarmsList

        recyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            val alarmsList = viewModel.generateAlarms(medId, alarmTiming)
            if (alarmsList != null) {
                val adapter = AlarmsListAdapter(requireContext(), alarmsList, this@AlarmsPerDayFragment)
                recyclerView.adapter = adapter
            }
        }

        // Lifecycle
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onAlarmUpdated(alarm: AlarmEntity) {
        lifecycleScope.launch {
            viewModel.updateAlarm(alarm)
        }
    }

    private fun changeValueText(value: Int) {
        val newText  = "Every $value hours"
        binding.valueText.text =newText
    }
}