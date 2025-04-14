package com.fpis.money.views.fragments.add.category.icon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R

// ColorAdapter.kt
class ColorAdapter(
    private val colors: List<Int>,
    private val onColorSelected: (colorRes: Int) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorView: View = itemView.findViewById(R.id.colorView)
        val selection: View = itemView.findViewById(R.id.selectionIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val colorRes = colors[position]
        val color = ContextCompat.getColor(holder.itemView.context, colorRes)

        holder.colorView.setBackgroundColor(color)
        holder.selection.visibility = if (position == selectedPosition) View.VISIBLE else View.INVISIBLE

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onColorSelected(colorRes)
        }
    }

    override fun getItemCount(): Int = colors.size
}
