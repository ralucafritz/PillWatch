package com.example.pillwatch.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.pillwatch.R
import com.example.pillwatch.data.model.MedsEntity
import java.util.Locale

class AutoCompleteAdapter (context: Context, medsList: List<MedsEntity>) :  ArrayAdapter<MedsEntity>(context, 0, medsList) {

    private var filteredMedsList: List<MedsEntity> = ArrayList()

    override fun getFilter(): Filter {
        return medFilter
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertViewVar = convertView

        if (convertViewVar == null) {
            convertViewVar = LayoutInflater.from(context).inflate(
                R.layout.autocomplete_item_place, parent, false
            )
        }

        val medLabel: TextView = convertViewVar!!.findViewById(R.id.autocomplete_item_place_label)

        val med = getItem(position)
        if (med != null) {
            medLabel.text = med.tradeName
        }

        return convertViewVar!!
    }

    private val medFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            filteredMedsList = ArrayList()

            filteredMedsList = if (constraint.isNullOrEmpty()) {
                medsList
            } else {
                val filterPattern = constraint.toString().lowercase(Locale.ROOT).trim()
                medsList.filter { it.tradeName.lowercase(Locale.ROOT).contains(filterPattern) }
            }

            results.values = filteredMedsList
            results.count = filteredMedsList.size

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            clear()
            val filteredList = results?.values as? List<MedsEntity> ?: emptyList()
            addAll(filteredList)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as MedsEntity).tradeName
        }
    }

}