package com.example.pillwatch.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.databinding.ItemHomeListBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeListAdapter(private val medList: MutableList<Pair<UserMedsEntity, Long>>) :
    RecyclerView.Adapter<HomeListAdapter.HomeViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HomeViewHolder {
        val binding =
            ItemHomeListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return HomeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return medList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(medList[position].first.tradeName, medList[position].second)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(medList[position].first.id)
        }
    }

    class HomeViewHolder(private val binding: ItemHomeListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(medName: String, nextAlarm: Long) {
            if (medName.length >= 15) {
                val truncated = medName.substring(0, 14) + "..."
                binding.medItemName.text = truncated
            } else {
                binding.medItemName.text = medName
            }
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val nextAlarmFormatted = dateFormat.format(Date(nextAlarm))
            binding.nextAlarm.text = nextAlarmFormatted

        }
    }
}