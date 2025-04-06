package com.fpis.money.views.fragments.records

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.databinding.FragmentRecordBinding
import com.fpis.money.views.fragments.records.placeholder.PlaceholderContent.PlaceholderItem

class RecordRecyclerViewAdapter(
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<RecordRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentRecordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.categoryName.text = item.id // или item.categoryName
        holder.cardHeader.text = item.content // или item.cardType
        holder.amountValue.text = "-₸25.56" // сюда подставь из item, если есть
        holder.date.text = "31 Aug 2023" // сюда тоже из item дату, если есть
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        val categoryName: TextView = binding.categoryName
        val cardHeader: TextView = binding.cardHeader
        val amountValue: TextView = binding.amountValue
        val date: TextView = binding.date
    }
}