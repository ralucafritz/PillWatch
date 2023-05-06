package com.example.pillwatch.ui.alarms

import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.databinding.ItemAlarmListBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmsListAdapter(
    private val context: Context,

    private val onAlarmUpdatedListener: OnAlarmUpdatedListener
) : RecyclerView.Adapter<AlarmsListAdapter.AlarmViewHolder>() {

    private val alarms: MutableList<AlarmEntity> = mutableListOf()

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    fun updateAlarms(newAlarms: MutableList<AlarmEntity>) {
        alarms.clear()
        alarms.addAll(newAlarms)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding =
            ItemAlarmListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarms[position])
    }

    override fun getItemCount(): Int = alarms.size

    inner class AlarmViewHolder(private val binding: ItemAlarmListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(alarm: AlarmEntity) {
            updateTimePickerText(alarm.timeInMillis)
            binding.enabledSwitch.isChecked = alarm.isEnabled

            binding.timePicker.setOnClickListener {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = alarm.timeInMillis
                }
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }
                        alarm.timeInMillis = calendar.timeInMillis
                        updateTimePickerText(calendar.timeInMillis)
                        onAlarmUpdatedListener.onAlarmUpdated(alarm)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            }

            binding.enabledSwitch.setOnCheckedChangeListener { _, isChecked ->
                alarm.isEnabled = isChecked
                binding.timePicker.isEnabled = isChecked
                onAlarmUpdatedListener.onAlarmUpdated(alarm)
            }
        }

        private fun updateTimePickerText(newTimeInMillis: Long) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = newTimeInMillis
            }
            binding.timePicker.text = timeFormat.format(calendar.time)
        }
    }
}