package com.example.pillwatch.ui.medication.medpage

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pillwatch.R
import com.example.pillwatch.data.model.MedsLogEntity
import com.example.pillwatch.databinding.ItemMedLogDialogBinding
import com.example.pillwatch.utils.TakenStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogsAdapter(
    private val context: Context,
    private val logsList: List<MedsLogEntity>
) :
    RecyclerView.Adapter<LogsAdapter.LogsViewHolder>() {

    var onItemClick: ((Long) -> Unit)? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LogsViewHolder {
        val binding =
            ItemMedLogDialogBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return LogsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return logsList.size
    }

    override fun onBindViewHolder(holder: LogsViewHolder, position: Int) {
        holder.bind(logsList[position], context)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(logsList[position].id)
        }
    }

    class LogsViewHolder(private val binding: ItemMedLogDialogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(log: MedsLogEntity, context: Context) {
            binding.timestamp.text = formatTimestamp(log.timestamp)
            binding.status.text = log.status.label

            val statusColor = getStatusColor(log.status, context)
            val backgroundDrawable = binding.status.background
            val colorFilter = PorterDuffColorFilter(statusColor, PorterDuff.Mode.SRC_ATOP)
            backgroundDrawable?.colorFilter = colorFilter

        }

        private fun formatTimestamp(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm E dd-MMM-yy ", Locale.getDefault())
            val date = Date(timestamp)
            return dateFormat.format(date)
        }

        private fun getStatusColor(status: TakenStatus, context: Context): Int {
            return when (status) {
                TakenStatus.TAKEN -> getColorFromAttribute(R.attr.colorTaken, context)
                TakenStatus.POSTPONED -> getColorFromAttribute(R.attr.colorPostponed, context)
                TakenStatus.MISSED -> getColorFromAttribute(R.attr.colorMissed, context)
            }
        }

        private fun getColorFromAttribute(attrId: Int, context: Context): Int {
            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(attrId, typedValue, true)
            return typedValue.data
        }
    }
}