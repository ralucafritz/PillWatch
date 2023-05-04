package com.example.pillwatch.ui.addmed

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.databinding.FragmentAlarmsPerDayBinding
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.utils.AlarmTiming
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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

        (requireActivity() as MainActivity).navBarToolbarBottomNav( false, R.id.alarmsPerDayFragment)

        // ViewModel
        binding.viewModel = viewModel

        viewModel.medId  = AlarmsPerDayFragmentArgs.fromBundle(requireArguments()).id
        viewModel.alarmTiming = AlarmsPerDayFragmentArgs.fromBundle(requireArguments()).alarmTiming

        binding.slider.isVisible = false
        if(viewModel.alarmTiming  == AlarmTiming.EVERY_X_HOURS) {
            binding.slider.isVisible = true
            binding.slider.max  = viewModel.values.size - 1
            val defaultValue = 3  // aka values[3] = 4
            binding.slider.progress = defaultValue
            viewModel.everyXHours.value =  viewModel.values[defaultValue]
            changeValueText( binding.slider.progress)
        }

        setupStartHour()

        binding.slider.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.everyXHours.value = viewModel.values[progress]
                changeValueText(viewModel.values[progress])
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        val recyclerView = binding.alarmsList

        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = AlarmsListAdapter(requireContext(), this@AlarmsPerDayFragment)
        recyclerView.adapter = adapter

       viewModel.alarmsList.observe(viewLifecycleOwner) {
           adapter.updateAlarms(viewModel.alarmsList.value!!)
        }
        viewModel.everyXHours.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                viewModel.generateAlarms()
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

    private fun setupStartHour() {
        viewModel.startHour.observe(viewLifecycleOwner) { startHour ->
            val startHourText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(startHour.time)
            binding.startHour.text = startHourText
        }

        val now = Calendar.getInstance()
        val minute = now.get(Calendar.MINUTE)
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val closestMinute = when (minute % 5) {
            0 -> minute
            else -> minute + (5 - (minute % 5))
        }
        viewModel.updateStartHour(hour, closestMinute)

        binding.startHour.setOnClickListener {
            val calendar = viewModel.startHour.value ?: Calendar.getInstance()
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    viewModel.updateStartHour(hourOfDay, minute)
                },
                hourOfDay,
                minute,
                true
            )
            timePickerDialog.show()
        }
    }

    private fun changeValueText(value: Int) {
        val newText  = "Every $value hours"
        binding.valueText.text =newText
    }
}