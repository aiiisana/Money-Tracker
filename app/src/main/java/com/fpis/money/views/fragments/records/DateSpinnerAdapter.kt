package com.fpis.money.views.fragments.records

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.fpis.money.R

class DateSpinnerAdapter(context: Context, private val data: ArrayList<String>) :
    ArrayAdapter<String>(context, R.layout.spinner_item, data) {

    var selectedDate: String? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        val textView: TextView = view.findViewById(R.id.spinner_text)

        textView.text = selectedDate ?: data[position]
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        val textView: TextView = view.findViewById(R.id.spinner_text)

        textView.text = data[position]
        return view
    }
}