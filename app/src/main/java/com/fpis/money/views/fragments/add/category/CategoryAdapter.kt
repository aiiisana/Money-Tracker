package com.fpis.money.views.fragments.add.category

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.models.Category
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoryAdapter(
    private var categories: List<Category>,
    private val onClick: (Category) -> Unit,
    private val dialog: BottomSheetDialogFragment
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.category_icon)
        val name: TextView = itemView.findViewById(R.id.category_name)
//        val arrow: ImageView = itemView.findViewById(R.id.category_checkbox)
    }

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.name.text = category.name
        holder.icon.setImageResource(category.iconRes)
        holder.itemView.setOnClickListener {
            onClick(category)
            dialog.dismiss()
        }
    }

    override fun getItemCount(): Int = categories.size
}