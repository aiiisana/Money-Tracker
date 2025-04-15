package com.fpis.money.views.fragments.add.category.icon

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R

class IconAdapter(
    private val icons: List<Int>,
    private val onIconSelected: (iconRes: Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.ViewHolder>() {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1
    private var currentColorRes: Int? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iconImage)
        val selection: View = itemView.findViewById(R.id.selectionIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_icon, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val iconRes = icons[position]
        holder.icon.setImageResource(iconRes)
        holder.selection.visibility = if (position == selectedPosition) View.VISIBLE else View.INVISIBLE

        if (position == selectedPosition && currentColorRes != null) {
            holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, currentColorRes!!))
        } else {
            holder.icon.clearColorFilter()
        }

        holder.itemView.setOnClickListener {
            lastSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(lastSelectedPosition)
            notifyItemChanged(selectedPosition)
            onIconSelected(iconRes)
        }
    }

    override fun getItemCount(): Int = icons.size

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyItemChanged(position)
    }

    fun getSelectedPosition(): Int = selectedPosition

    fun setCurrentColor(colorRes: Int?) {
        currentColorRes = colorRes
        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition)
        }
    }

    fun updateIcons(newIcons: List<Int>) {
        val diffCallback = IconDiffCallback(icons, newIcons)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
    }

    class IconDiffCallback(
        private val oldList: List<Int>,
        private val newList: List<Int>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean =
            oldList[oldPos] == newList[newPos]
        override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean =
            oldList[oldPos] == newList[newPos]
    }
}
