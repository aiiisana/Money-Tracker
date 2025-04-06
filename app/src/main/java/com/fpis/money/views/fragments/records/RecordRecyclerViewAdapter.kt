package com.fpis.money.views.fragments.records

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.databinding.FragmentRecordBinding
import com.fpis.money.models.Transaction

class RecordRecyclerViewAdapter(
    private var values: List<Transaction>
) : RecyclerView.Adapter<RecordRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentRecordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.categoryName.text = item.category
        holder.cardHeader.text = item.subCategory
        holder.amountValue.text = "-₸${item.amount}"
        holder.date.text = item.date.toString()
    }

    override fun getItemCount(): Int = values.size

    fun updateData(newValues: List<Transaction>) {
        values = newValues
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: FragmentRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        val categoryName: TextView = binding.categoryName
        val cardHeader: TextView = binding.cardHeader
        val amountValue: TextView = binding.amountValue
        val date: TextView = binding.date
    }
}