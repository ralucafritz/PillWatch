package com.example.pillwatch.ui.alarms

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.alarms.AlarmHandler
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.databinding.FragmentAlarmsPerDayBinding
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.utils.AlarmTiming
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class AlarmsPerDayFragment : Fragment(), OnAlarmUpdatedListener {

    // view binding for the fragment
    private lateinit var binding: FragmentAlarmsPerDayBinding

    // viewModel for the fragment, injected using Dagger
    @Inject
    lateinit var viewModel: AlarmsViewModel

    @Inject
    lateinit var alarmHandler: AlarmHandler

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
        // inflate the fragment layout
        binding = FragmentAlarmsPerDayBinding.inflate(inflater)

        // set up the bottom navigation bar in the parent activity
        (requireActivity() as MainActivity).navBarToolbarBottomNav(false, R.id.alarmsPerDayFragment)

        // bind the ViewModel to the view
        binding.viewModel = viewModel

        // initialize ViewModel args from fragment arguments
        setupViewModelFromArgs()

        // initialize the SeekBar component
        setupSeekBar()

        // set the start hour value to the current hour and the nearest 5-minute increment.
        setupStartHour()

        // set up the RecyclerView to display the alarms
        val recyclerView = binding.alarmsList

        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = AlarmsListAdapter(requireContext(), this@AlarmsPerDayFragment)
        recyclerView.adapter = adapter

        // update the alarms list in the adapter when it changes in the ViewModel
        viewModel.alarmsList.observe(viewLifecycleOwner) {
            adapter.updateAlarms(viewModel.alarmsList.value!!)
        }

        // launch the generateAlarms() coroutine when the everyXHours value changes in the ViewModel
        viewModel.everyXHours.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                viewModel.generateAlarms()
            }
        }

        binding.buttonNext.setOnClickListener {
            viewModel.alarmsList.value!!.forEach { alarm ->
                if (alarm.isEnabled) {
                    alarmHandler.scheduleAlarm(alarm.id.toInt(), alarm.timeInMillis)
                }
            }
            this.findNavController().navigate(
                AlarmsPerDayFragmentDirections.actionAlarmsPerDayFragmentToMedPageFragment(
                    viewModel.medId
                )
            )
        }

        // Lifecycle
        binding.lifecycleOwner = this

        // return the root view of the fragment
        return binding.root
    }

    //  called when an alarm is updated by the user manually -> the other alarms do not get autogenerated
    //  but they do get sorted based on the next possible alarm
    override fun onAlarmUpdated(alarm: AlarmEntity) {
        lifecycleScope.launch {
            viewModel.updateAlarm(alarm)
        }
    }

    // set the start hour value to the current hour and the nearest 5-minute increment.
    private fun setupStartHour() {
        // update the start hour text in the view when the start hour value changes in the ViewModel
        viewModel.startHour.observe(viewLifecycleOwner) { startHour ->
            val startHourText =
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(startHour.time)
            binding.startHour.text = startHourText
        }

        val now = Calendar.getInstance()
        val minute = now.get(Calendar.MINUTE)
        var hour = now.get(Calendar.HOUR_OF_DAY)
        var closestMinute = when (minute % 5) {
            0 -> minute + 5
            else -> minute + (5 - (minute % 5))
        }
        if (closestMinute >= 60) {
            closestMinute %= 60
            hour += 1
            if (hour == 24) {
                hour = 0
            }
        }
        viewModel.updateStartHour(hour, closestMinute)

        // show a time picker dialog when the start hour text is clicked, allowing the user to update the start hour value
        binding.startHour.setOnClickListener {
            val calendar = viewModel.startHour.value ?: Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    viewModel.updateStartHour(hourOfDay, minute)
                },
                currentHour,
                currentMinute,
                true
            )
            timePickerDialog.show()
        }
    }

    // update the text of the value component based on the current value of the SeekBar
    private fun changeValueText(value: Int) {
        val newText = "Every $value hours"
        binding.valueText.text = newText
    }

    // initialize ViewModel args from fragment arguments
    private fun setupViewModelFromArgs() {
        viewModel.medId = AlarmsPerDayFragmentArgs.fromBundle(requireArguments()).id
        viewModel.alarmTiming = AlarmsPerDayFragmentArgs.fromBundle(requireArguments()).alarmTiming
    }

    // initialize the SeekBar component
    private fun setupSeekBar() {
        binding.slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.everyXHours.value = viewModel.values[progress]
                changeValueText(viewModel.values[progress])
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        // set up the SeekBar to be visible and initialized to a default value when the alarm timing is EVERY_X_HOURS
        if (viewModel.alarmTiming == AlarmTiming.EVERY_X_HOURS) {
            binding.slider.isVisible = true
            binding.slider.max = viewModel.values.size - 1
            val defaultValue = 3 // aka values[3] = 4
            binding.slider.progress = defaultValue
            viewModel.everyXHours.value = viewModel.values[defaultValue]
            changeValueText(binding.slider.progress)
        } else {
            binding.slider.isVisible = false
        }
    }

}