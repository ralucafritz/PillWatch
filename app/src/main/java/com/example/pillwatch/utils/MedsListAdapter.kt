package com.example.pillwatch.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pillwatch.R
import com.example.pillwatch.data.model.UserMedsEntity

class MedsListAdapter(private val medList: List<UserMedsEntity>): RecyclerView.Adapter<MedsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_view_design, viewGroup, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return medList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.medItem.text = medList[position].tradeName
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView
        val medItem: TextView

        init {
            img = view.findViewById(R.id.pill_image_med_item)
            medItem = view.findViewById(R.id.med_item_name)
        }
    }
}