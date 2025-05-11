package com.fpis.money.views.fragments.spendinglimitpackage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.models.Budget
import java.text.NumberFormat
import java.util.*

class SpendingLimitAdapter(
    private val onEditClick: (Budget) -> Unit,
    private val onDeleteClick: (Budget) -> Unit
) : ListAdapter<Budget, SpendingLimitAdapter.LimitViewHolder>(LIMIT_COMPARATOR) {

    inner class LimitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val categoryIcon: ImageView = view.findViewById(R.id.category_icon)
        private val categoryText: TextView = view.findViewById(R.id.tv_category)
        private val limitAmountText: TextView = view.findViewById(R.id.tv_limit_amount)
        private val spentAmountText: TextView = view.findViewById(R.id.tv_spent_amount)
        private val progressBar: ProgressBar = view.findViewById(R.id.progress_limit)
        private val progressText: TextView = view.findViewById(R.id.tv_limit_progress)
        private val warningText: TextView = view.findViewById(R.id.tv_warning)
        private val editButton: ImageView = view.findViewById(R.id.btn_edit_limit)
        private val deleteButton: ImageView = view.findViewById(R.id.btn_delete_limit)

        fun bind(limit: Budget) {
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
                currency = Currency.getInstance("KZT")
                maximumFractionDigits = 0
            }

            categoryText.text = limit.category
            categoryIcon.setImageResource(limit.iconRes)
            limit.colorRes.let { colorRes ->
                categoryIcon.setColorFilter(ContextCompat.getColor(itemView.context, colorRes))
            }

            limitAmountText.text = currencyFormat.format(limit.amount).replace("KZT", "₸")
            spentAmountText.text = "${currencyFormat.format(limit.spent).replace("KZT", "₸")} spent"

            val progress = limit.percentage
            progressBar.progress = progress
            progressText.text = "$progress%"

            val progressColor = when {
                progress >= 100 -> R.color.red
                progress >= 80 -> R.color.orange
                else -> R.color.green
            }
            progressBar.progressTintList = ContextCompat.getColorStateList(itemView.context, progressColor)
            progressText.setTextColor(ContextCompat.getColor(itemView.context, progressColor))

            warningText.visibility = if (limit.isLimitExceeded) View.VISIBLE else View.GONE

            editButton.setOnClickListener { onEditClick(limit) }
            deleteButton.setOnClickListener { onDeleteClick(limit) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LimitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spending_limit, parent, false)
        return LimitViewHolder(view)
    }

    override fun onBindViewHolder(holder: LimitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val LIMIT_COMPARATOR = object : DiffUtil.ItemCallback<Budget>() {
            override fun areItemsTheSame(oldItem: Budget, newItem: Budget): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Budget, newItem: Budget): Boolean {
                return oldItem == newItem
            }
        }
    }
}