package com.fpis.money.views.fragments.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.models.Transaction

class FavoritesAdapter(
    private val onClick: (Transaction) -> Unit,
    private val onLongClick: (Transaction) -> Unit
) : ListAdapter<Transaction, FavoritesAdapter.FavoriteViewHolder>(DiffCallback()) {

    class FavoriteViewHolder(view: View, onClick: (Transaction) -> Unit, onLongClick: (Transaction) -> Unit) : RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(R.id.favorite_name)
        private val amountView: TextView = view.findViewById(R.id.favorite_amount)
        private var currentFavorite: Transaction? = null

        init {
            view.setOnClickListener {
                currentFavorite?.let { onClick(it) }
            }

            view.setOnLongClickListener {
                currentFavorite?.let {
                    onLongClick(it)
                    true
                } ?: false
            }
        }

        fun bind(favorite: Transaction) {
            currentFavorite = favorite
            nameView.text = favorite.category
            amountView.text = if (favorite.type == "expense") {
                "-₸${favorite.amount}"
            } else {
                "₸${favorite.amount}"
            }
            amountView.setTextColor(
                ContextCompat.getColor(itemView.context,
                if (favorite.type == "expense") R.color.red else R.color.green))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_transaction, parent, false)
        return FavoriteViewHolder(view, onClick, onLongClick)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}