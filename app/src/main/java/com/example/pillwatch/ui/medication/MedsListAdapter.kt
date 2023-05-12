package com.example.pillwatch.ui.medication

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pillwatch.R
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.databinding.ItemMedsListBinding

class MedsListAdapter(
    private val context: Context,
    private val medList: List<UserMedsEntity>,
    private val logList: List<List< Long>>
    ) :
    RecyclerView.Adapter<MedsListAdapter.MedsViewHolder>() {

    var onItemClick: ((Long) -> Unit)? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MedsViewHolder {
        val binding =
            ItemMedsListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return MedsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return medList.size
    }

    override fun onBindViewHolder(holder: MedsViewHolder, position: Int) {
        holder.bind(medList[position], logList[position], context)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(medList[position].id)
        }
    }

    class MedsViewHolder(private val binding: ItemMedsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(med: UserMedsEntity, logs: List<Long>, context: Context) {
            // Set the truncated or full medication name based on the length
            if (med.tradeName.length >= 15) {
                val truncated = med.tradeName.substring(0, 14) + "..."
                binding.medItemName.text = truncated
            } else {
                binding.medItemName.text = med.tradeName
            }
            // Set the medication concentration
            binding.medItemConc.text = med.concentration

            // Set the image and color filter for the FAB based on the medication's medId
            if (med.medId != null) {
                binding.medItemFab.setImageResource(R.drawable.ic_check)
                binding.medItemFab.setColorFilter(ContextCompat.getColor(context, R.color.green))
            } else {
                binding.medItemFab.setImageResource(R.drawable.ic_close)
                binding.medItemFab.setColorFilter(ContextCompat.getColor(context, R.color.red))
            }
            // Set the log counts for taken, postponed, and missed
            if (logs.isNotEmpty()) {
                val textTaken = "${logs[0] ?: 0} taken"
                val textPostponed = "${logs[1] ?: 0} postponed"
                val textMissed =  "${logs[2] ?: 0} missed"
                binding.takenLog.text = textTaken
                binding.postponedLog.text = textPostponed
                binding.missedLog.text = textMissed
            }
        }
    }
}