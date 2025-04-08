package com.fpis.money.views.fragments.records

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.databinding.FragmentRecordBinding
import com.fpis.money.models.Transaction
import java.text.SimpleDateFormat
import java.util.*

class RecordRecyclerViewAdapter(
    private var values: List<Transaction>,
    private val onDelete: (Transaction) -> Unit
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

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(item.date)
        holder.date.text = formattedDate

        if (item.type == "income") {
            holder.amountValue.setTextColor(holder.itemView.context.getColor(R.color.green))
            holder.amountValue.text = "₸${item.amount}"
        } else {
            holder.amountValue.setTextColor(holder.itemView.context.getColor(R.color.red))
            holder.amountValue.text = "-₸${item.amount}"
        }
    }

    override fun getItemCount(): Int = values.size

    fun updateData(newValues: List<Transaction>) {
        values = newValues
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val filteredList = values.filter { transaction ->
            transaction.category.contains(query, ignoreCase = true) ||
                    transaction.subCategory.contains(query, ignoreCase = true) ||
                    transaction.amount.toString().contains(query) ||
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(transaction.date).contains(query)
        }
        updateData(filteredList)
    }

    inner class ViewHolder(binding: FragmentRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        val categoryName: TextView = binding.categoryName
        val cardHeader: TextView = binding.cardHeader
        val amountValue: TextView = binding.amountValue
        val date: TextView = binding.date

        init {
            itemView.setOnClickListener {
                val transaction = values[adapterPosition]
                onDelete(transaction)
            }
        }
    }
}