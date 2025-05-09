package com.fpis.money.views.fragments.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.fpis.money.R
import com.fpis.money.views.fragments.home.stats.StatisticsFragment.CategoryStats
import java.text.NumberFormat
import java.util.*

class HomeCategoryAdapter :
    RecyclerView.Adapter<HomeCategoryAdapter.ViewHolder>() {

    private val items = mutableListOf<CategoryStats>()
    private val fmt   = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }

    fun submit(list: List<CategoryStats>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_category, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val tvName   = v.findViewById<TextView>(R.id.tv_category_name)
        private val tvAmount = v.findViewById<TextView>(R.id.tv_category_spent)
        private val ring     = v.findViewById<CircularProgressIndicator>(R.id.progress_ring)

        fun bind(cat: CategoryStats) {
            tvName.text   = cat.name
            tvAmount.text = "₸${fmt.format(cat.amount)} spent"

            ring.apply {
                isIndeterminate = false
                max = 100
                setProgressCompat(cat.progress, false)
                setIndicatorColor(Color.WHITE)
                trackColor =
                    ColorUtils.setAlphaComponent(Color.WHITE, /* 35% */ 90)
            }

            val fill = ColorUtils.blendARGB(cat.color, Color.WHITE, 0.30f)
            itemView.background?.setTint(fill)
        }
    }
}
