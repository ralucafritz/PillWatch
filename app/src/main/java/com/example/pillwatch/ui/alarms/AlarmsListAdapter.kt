package com.example.pillwatch.ui.alarms

import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pillwatch.alarms.OnAlarmUpdatedListener
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.databinding.ItemAlarmListBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Adapter class for displaying the list of alarms in a RecyclerView.
 *
 * @param context The context of the activity or fragment using the adapter.
 * @param onAlarmUpdatedListener The listener for handling alarm update events.
 */
class AlarmsListAdapter(
    private val context: Context,

    private val onAlarmUpdatedListener: OnAlarmUpdatedListener
) : RecyclerView.Adapter<AlarmsListAdapter.AlarmViewHolder>() {

    private var alarms: List<AlarmEntity> = mutableListOf()

    // Time format for displaying alarm time
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    /**
     * Updates the list of alarms in the adapter.
     *
     * @param newAlarms The new list of alarms to be displayed.
     */
    fun updateAlarms(newAlarms: List<AlarmEntity>) {
        alarms = newAlarms
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlarmViewHolder {
        // Inflate the layout for each item in the list
        val binding =
            ItemAlarmListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        // Bind the alarm entity to the view holder
        holder.bind(alarms[position], position)
    }

    override fun getItemCount(): Int = alarms.size

    inner class AlarmViewHolder(private val binding: ItemAlarmListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds the alarm entity to the view holder.
         *
         * @param alarm The alarm entity to be displayed.
         */
        fun bind(alarm: AlarmEntity, position: Int) {
            // Update the text and state of the time picker
            updateTimePickerText(alarm.timeInMillis)
            binding.enabledSwitch.isChecked = alarm.isEnabled

            // Set click listener for the time picker
            binding.timePicker.setOnClickListener {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = alarm.timeInMillis
                }
                // Show a time picker dialog for selecting the alarm time
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }
                        val currentTime = Calendar.getInstance()
                        val selectedTime = calendar.clone() as Calendar

                        if(selectedTime.get(Calendar.HOUR_OF_DAY) >= currentTime.get(Calendar.HOUR_OF_DAY) &&
                            selectedTime.get(Calendar.DAY_OF_MONTH) != currentTime.get(Calendar.DAY_OF_MONTH))
                        {
                            selectedTime.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH))
                        } else if(selectedTime.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH)) {
                            selectedTime.add(Calendar.DAY_OF_MONTH, 1)
                        }

                        Timber.d(alarm.timeInMillis.toString())
                        alarm.timeInMillis = selectedTime.timeInMillis
                        updateTimePickerText(calendar.timeInMillis)
                        // Notify the listener that the alarm has been updated
                        onAlarmUpdatedListener.onAlarmUpdated(alarm)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            }
            // Set listener for the enabled switch
            binding.enabledSwitch.setOnCheckedChangeListener { _, isChecked ->
                alarm.isEnabled = isChecked
                binding.timePicker.isEnabled = isChecked
                // Notify the listener that the alarm has been updated
                onAlarmUpdatedListener.onAlarmUpdated(alarm)
            }
        }

        /**
         * Updates the text of the time picker with the given time.
         *
         * @param newTimeInMillis The time in milliseconds to be displayed.
         */
        private fun updateTimePickerText(newTimeInMillis: Long) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = newTimeInMillis
            }
            binding.timePicker.text = timeFormat.format(calendar.time)
        }
    }
}