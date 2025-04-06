package com.fpis.money.views.fragments.add

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoryAdapter(
    private val categories: List<String>,
    private val onClick: (String) -> Unit,
    private val dialog: BottomSheetDialogFragment
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private val iconMap = mapOf(
        "Food & Drink" to R.drawable.ic_food_drink,
        "Shopping" to R.drawable.ic_shopping,
        "Health" to R.drawable.health,
        "Transport" to R.drawable.ic_bus,
        "Interest" to R.drawable.ic_interest,
        "Life & Event" to R.drawable.ic_event,
        "Interest" to R.drawable.ic_interest,
        "Add New Category" to R.drawable.ic_add
    )

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.category_icon)
        val name: TextView = itemView.findViewById(R.id.category_name)
        val arrow: ImageView = itemView.findViewById(R.id.arrow_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.name.text = category
        val iconRes = iconMap[category] ?: R.drawable.ic_food
        holder.icon.setImageResource(iconRes)
        holder.itemView.setOnClickListener {
            Log.d("CategoryClick", "Clicked: $category")
            onClick(category)
            dialog.dismiss()
        }
    }

    override fun getItemCount(): Int = categories.size
}