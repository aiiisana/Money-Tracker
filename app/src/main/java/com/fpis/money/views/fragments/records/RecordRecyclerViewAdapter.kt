package com.fpis.money.views.fragments.records

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.databinding.FragmentRecordBinding
import com.fpis.money.models.TransactionItem
import java.text.SimpleDateFormat
import java.util.*

class RecordRecyclerViewAdapter(
    private var values: List<TransactionItem>,
    private val fragmentManager: FragmentManager,
    private val onDelete: (TransactionItem) -> Unit
) : RecyclerView.Adapter<RecordRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentRecordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        when (item) {
            is TransactionItem.RecordItem -> {
                val record = item.record
                holder.categoryName.text = record.category
                holder.cardHeader.text = record.subCategory

                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                holder.date.text = dateFormat.format(record.date)

                if (record.type == "income") {
                    holder.amountValue.setTextColor(holder.itemView.context.getColor(R.color.green))
                    holder.amountValue.text = "₸${record.amount}"
                } else {
                    holder.amountValue.setTextColor(holder.itemView.context.getColor(R.color.red))
                    holder.amountValue.text = "-₸${record.amount}"
                }
            }

            is TransactionItem.TransferItem -> {
                val transfer = item.transfer
                holder.categoryName.text = "Transfer"
                holder.cardHeader.text = "${transfer.fromAccount} → ${transfer.toAccount}"

                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                holder.date.text = dateFormat.format(transfer.date)

                holder.amountValue.setTextColor(holder.itemView.context.getColor(R.color.white))
                holder.amountValue.text = "₸${transfer.amount}"
            }
        }

        holder.itemView.setOnLongClickListener {
            onDelete(item)
            true
        }
    }

    override fun getItemCount(): Int = values.size

    fun updateData(newValues: List<TransactionItem>) {
        values = newValues
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        val filteredList = values.filter { item ->
            when (item) {
                is TransactionItem.RecordItem -> {
                    val t = item.record
                    t.category.contains(query, true) ||
                            t.subCategory.contains(query, true) ||
                            t.amount.toString().contains(query)
                }

                is TransactionItem.TransferItem -> {
                    val tr = item.transfer
                    tr.fromAccount.contains(query, true) ||
                            tr.toAccount.contains(query, true) ||
                            tr.amount.toString().contains(query)
                }
            }
        }
        updateData(filteredList)
    }

    inner class ViewHolder(binding: FragmentRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        val categoryName: TextView = binding.categoryName
        val cardHeader: TextView = binding.cardHeader
        val amountValue: TextView = binding.amountValue
        val date: TextView = binding.date
    }
}