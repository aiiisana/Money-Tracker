package com.fpis.money.views.fragments.cards

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.fpis.money.R

import com.fpis.money.views.fragments.cards.placeholder.PlaceholderContent.PlaceholderItem

class MyCardRecyclerViewAdapter(
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<MyCardRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use the new item_card.xml layout
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_card, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        // Set card title based on position
        if (position % 2 == 0) {
            holder.cardTitle.text = "My Credit Card"
            holder.cardContent.setCardBackgroundColor(holder.itemView.context.getColor(R.color.credit_card_blue))
        } else {
            holder.cardTitle.text = "My Debit Card"
            holder.cardContent.setCardBackgroundColor(holder.itemView.context.getColor(R.color.debit_card_orange))
        }

        // Set card details
        holder.cardNumber1.text = "* * * *"
        holder.cardNumber2.text = "* * * *"
        holder.cardNumber3.text = "* * * *"
        holder.cardNumber4.text = "1234"
        holder.cardHolder.text = "John Doe"
        holder.expireDate.text = "**/**"
        holder.cvv.text = "CVV: ***"

        // Set click listeners
        holder.visibilityIcon.setOnClickListener {
            // Toggle card number visibility
        }

        holder.moreIcon.setOnClickListener {
            // Show options menu
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardTitle: TextView = view.findViewById(R.id.tv_card_title)
        val cardContent: CardView = view.findViewById(R.id.card_content)
        val cardNumber1: TextView = view.findViewById(R.id.tv_card_group1)
        val cardNumber2: TextView = view.findViewById(R.id.tv_card_group2)
        val cardNumber3: TextView = view.findViewById(R.id.tv_card_group3)
        val cardNumber4: TextView = view.findViewById(R.id.tv_card_group4)
        val cardHolder: TextView = view.findViewById(R.id.tv_card_holder)
        val expireDate: TextView = view.findViewById(R.id.tv_expire_date)
        val cvv: TextView = view.findViewById(R.id.tv_cvv)
        val visibilityIcon: ImageView = view.findViewById(R.id.iv_visibility)
        val moreIcon: ImageView = view.findViewById(R.id.iv_more)
    }
}