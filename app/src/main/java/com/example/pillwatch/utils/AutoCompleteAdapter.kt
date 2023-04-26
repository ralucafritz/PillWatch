package com.example.pillwatch.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.pillwatch.R
import java.util.Locale

class AutoCompleteAdapter (private val context: Context, private val medsData: Pair<List<String>, List<String>>) :  BaseAdapter(), Filterable {

    private val medConcs = medsData.second
    private val medNames = medsData.first
    private var filteredData: List<String> = medsData.first

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertViewVar = convertView

        if (convertViewVar == null) {
            convertViewVar = LayoutInflater.from(context).inflate(
                R.layout.autocomplete_item_place, parent, false
            )
        }

        val medLabel: TextView = convertViewVar!!.findViewById(R.id.autocomplete_item_med_label)
        val concLabel: TextView = convertViewVar!!.findViewById(R.id.autocomplete_item_conc_label)

        if(medNames.isNotEmpty() && medConcs.isNotEmpty()) {
            val medName = medNames.getOrNull(position)
            val medConc = medConcs.getOrNull(position)

            medLabel.text = medName ?: ""
            concLabel.text = medConc ?: ""

        }

        return convertViewVar!!
    }

    override fun getItem(position: Int): Any {
        return medsData.first[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return medNames.size
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val dropDownHeight =  200
        parent.layoutParams.height = dropDownHeight
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                results.values = medNames
                results.count = medNames.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }
}