package com.fpis.money.views.fragments.add.category.subcategory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.models.Subcategory

class SubcategoryAdapter(
    private var subcategories: List<Subcategory>,
    private val categoryIconRes: Int,
    private val categoryColorRes: Int,
    private val onAddClicked: () -> Unit,
    private val onSubcategoryClicked: (Subcategory) -> Unit
) : RecyclerView.Adapter<SubcategoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.subcategory_icon)
        val name: TextView = itemView.findViewById(R.id.subcategory_label)
        val container: LinearLayout = itemView.findViewById(R.id.subcategory_container)
    }

    fun updateSubcategories(newSubcategories: List<Subcategory>) {
        subcategories = newSubcategories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subcategory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == subcategories.size) {
            holder.icon.setImageResource(R.drawable.ic_add)
            holder.icon.clearColorFilter()
            holder.name.text = "Add"
            holder.container.setOnClickListener { onAddClicked() }
        } else {
            val subcategory = subcategories[position]
            holder.icon.setImageResource(categoryIconRes)
            holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, categoryColorRes))
            holder.name.text = subcategory.name
            holder.container.setOnClickListener { onSubcategoryClicked(subcategory) }
        }
    }

    override fun getItemCount(): Int = subcategories.size + 1
}