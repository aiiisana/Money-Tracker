package com.fpis.money.views.fragments.home.budgets

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.databinding.ItemBudgetBinding
import com.fpis.money.models.Budget
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.absoluteValue

class BudgetAdapter(
    private val onMenuClick: (Budget, View) -> Unit
) : ListAdapter<Budget, BudgetAdapter.BudgetViewHolder>(BudgetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BudgetViewHolder(
        private val binding: ItemBudgetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(budget: Budget) {
            val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }

//            if (budget.shouldNotify()) {
//                binding.notificationIndicator.visibility = View.VISIBLE
//                binding.notificationIndicator.setBackgroundColor(Color.YELLOW)
//            } else {
//                binding.notificationIndicator.visibility = View.GONE
//            }

            // Set category name and color
            binding.tvBudgetCategory.text = budget.category
            binding.tvBudgetCategory.setTextColor(Color.parseColor(budget.color))

            // Set budget amount and percentage
            binding.tvBudgetAmount.text = "₸${formatter.format(budget.amount)}"
            binding.tvBudgetPercentage.text = "%${budget.percentage}"

            // Set progress bar
            binding.progressBar.progress = budget.percentage.coerceAtMost(100)

            // Change progress bar color if overspending
            if (budget.isOverspending) {
                binding.progressBar.progressDrawable =
                    itemView.context.getDrawable(R.drawable.progress_bar_red)
            } else {
                binding.progressBar.progressDrawable =
                    itemView.context.getDrawable(R.drawable.progress_bar_blue)
            }

            // Set spent and left amounts
            binding.tvSpent.text = "-₸${formatter.format(budget.spent)} spent"

            if (budget.isOverspending) {
                binding.tvLeft.text = "₸${formatter.format(budget.remaining.absoluteValue)} overspending"
                binding.tvLeft.setTextColor(Color.parseColor("#FF4D4D"))
            } else {
                binding.tvLeft.text = "₸${formatter.format(budget.remaining)} left"
                binding.tvLeft.setTextColor(Color.WHITE)
            }

            // Set menu click listener
            binding.btnBudgetMenu.setOnClickListener {
                onMenuClick(budget, it)
            }
            binding.progressBar.secondaryProgress = budget.notificationThreshold
        }
    }

    class BudgetDiffCallback : DiffUtil.ItemCallback<Budget>() {
        override fun areItemsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Budget, newItem: Budget): Boolean {
            return oldItem == newItem
        }
    }
}