package com.fpis.money.views.fragments.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.models.Transfer

class FavoriteTransfersAdapter(
    private val onClick: (Transfer) -> Unit,
    private val onLongClick: (Transfer) -> Unit
) : ListAdapter<Transfer, FavoriteTransfersAdapter.FavoriteTransferViewHolder>(DiffCallback())
{

class FavoriteTransferViewHolder(view: View, onClick: (Transfer) -> Unit, onLongClick: (Transfer) -> Unit) : RecyclerView.ViewHolder(view) {
        private val labelView: TextView = view.findViewById(R.id.transfer_label)
        private val amountView: TextView = view.findViewById(R.id.transfer_amount)
        private var currentTransfer: Transfer? = null

        init {
            view.setOnClickListener {
                currentTransfer?.let { onClick(it) }
            }
            view.setOnLongClickListener {
                currentTransfer?.let {
                    onLongClick(it)
                    true
                } ?: false
            }
        }


        fun bind(transfer: Transfer) {
            currentTransfer = transfer
            labelView.text = "${transfer.fromAccount} → ${transfer.toAccount}"
            amountView.text = "₸${transfer.amount}"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Transfer>() {
        override fun areItemsTheSame(oldItem: Transfer, newItem: Transfer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transfer, newItem: Transfer): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteTransferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_transfer, parent, false)
        return FavoriteTransferViewHolder(view, onClick, onLongClick)
    }

    override fun onBindViewHolder(holder: FavoriteTransferViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
