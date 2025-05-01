package com.fpis.money.views.fragments.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.models.Budget
import com.google.android.material.progressindicator.CircularProgressIndicator

class BudgetCategoryAdapter : RecyclerView.Adapter<BudgetCategoryAdapter.VH>() {
    private var items = listOf<Budget>()
    fun submit(list: List<Budget>) {
        items = list; notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, v: Int) = VH(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_category, parent, false)
    )

    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, i: Int) = h.bind(items[i])

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName  = view.findViewById<TextView>(R.id.tv_category_name)
        private val tvSpent = view.findViewById<TextView>(R.id.tv_category_spent)
        private val ring    = view.findViewById<CircularProgressIndicator>(R.id.progress_ring)

        fun bind(b: Budget) {
            // text
            tvName.text  = b.category
            tvSpent.text = "₸%,.0f spent".format(b.spent)

            // ring
            ring.apply {
                isIndeterminate = false
                max = 100
                setProgressCompat(b.percentage, false)
                setIndicatorColor(Color.WHITE)
                trackColor = ColorUtils.setAlphaComponent(Color.WHITE, 90)
            }

            // background tint
            val base = Color.parseColor(b.color)
            val fill = ColorUtils.blendARGB(base, Color.WHITE, 0.30f)
            itemView.background?.setTint(fill)
        }
    }
}