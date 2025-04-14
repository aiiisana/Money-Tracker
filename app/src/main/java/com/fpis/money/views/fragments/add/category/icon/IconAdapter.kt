package com.fpis.money.views.fragments.add.category.icon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R

class IconAdapter(
    private val icons: List<Int>,
    private val onIconSelected: (iconRes: Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iconImage)
        val selection: View = itemView.findViewById(R.id.selectionIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_icon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val iconRes = icons[position]
        holder.icon.setImageResource(iconRes)
        holder.selection.visibility = if (position == selectedPosition) View.VISIBLE else View.INVISIBLE

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onIconSelected(iconRes)
        }
    }

    override fun getItemCount(): Int = icons.size
}