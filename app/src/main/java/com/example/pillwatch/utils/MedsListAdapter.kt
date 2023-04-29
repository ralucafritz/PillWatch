package com.example.pillwatch.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pillwatch.R
import com.example.pillwatch.data.model.UserMedsEntity

class MedsListAdapter(private val context: Context, private val medList: List<UserMedsEntity>): RecyclerView.Adapter<MedsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_view_design, viewGroup, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return medList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val med = medList[position]
        holder.img.inflate()
        holder.medItemName.text = med.tradeName
        holder.medItemConc.text = med.concentration
        if(med.medId != null) {
            holder.medItemFab.setImageResource(R.drawable.ic_check)
            holder.medItemFab.setColorFilter(ContextCompat.getColor(context, R.color.green))
        } else {
            holder.medItemFab.setImageResource(R.drawable.ic_close)
            holder.medItemFab.setColorFilter(ContextCompat.getColor(context, R.color.red))
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ViewStub
        val medItemName: TextView
        val medItemConc: TextView
        val medItemFab: ImageView


        init {
            img = view.findViewById(R.id.pill_image_med_item)
            medItemName = view.findViewById(R.id.med_item_name)
            medItemConc = view.findViewById(R.id.med_item_conc)
            medItemFab = view.findViewById(R.id.med_item_fab)
        }
    }
}