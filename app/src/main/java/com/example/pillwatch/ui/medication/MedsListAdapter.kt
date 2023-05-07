package com.example.pillwatch.ui.medication

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pillwatch.R
import com.example.pillwatch.data.model.MedsEntity
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.databinding.ItemMedsListBinding
import com.example.pillwatch.utils.OnItemClickListener

class MedsListAdapter(private val context: Context, private val medList: List<UserMedsEntity>) :
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
        holder.bind(medList[position], context)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(medList[position].id)
        }
    }

    class MedsViewHolder(private val binding: ItemMedsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(med: UserMedsEntity, context: Context) {
            if (med.tradeName.length >= 15) {
                val truncated = med.tradeName.substring(0, 14) + "..."
                binding.medItemName.text = truncated
            } else {
                binding.medItemName.text = med.tradeName
            }
            binding.medItemConc.text = med.concentration
            if (med.medId != null) {
                binding.medItemFab.setImageResource(R.drawable.ic_check)
                binding.medItemFab.setColorFilter(ContextCompat.getColor(context, R.color.green))
            } else {
                binding.medItemFab.setImageResource(R.drawable.ic_close)
                binding.medItemFab.setColorFilter(ContextCompat.getColor(context, R.color.red))
            }
        }
    }
}