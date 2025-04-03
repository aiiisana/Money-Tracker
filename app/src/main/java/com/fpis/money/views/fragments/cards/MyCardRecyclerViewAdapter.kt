package com.fpis.money.views.fragments.cards

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fpis.money.R

import com.fpis.money.views.fragments.cards.placeholder.PlaceholderContent.PlaceholderItem
import com.fpis.money.databinding.FragmentCardBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyCardRecyclerViewAdapter(
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<MyCardRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate your new layout (item_card.xml)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        // For demonstration, just binding your placeholder data
        // But you can adapt it to real card info

        holder.cardTitle.text = "My Credit Card" // or item.content
        val cardNumber = "**** **** **** 1234"
        // Split the card number into 4 parts
        val cardNumberParts = cardNumber.split(" ")

        holder.cardNumberPart1.text = cardNumberParts[0]
        holder.cardNumberPart2.text = cardNumberParts[1]
        holder.cardNumberPart3.text = cardNumberParts[2]
        holder.cardNumberPart4.text = cardNumberParts[3]

        holder.cardHolder.text = "John Doe"
        holder.expireDate.text = "Exp: 12/24"
        holder.cvv.text = "CVV: 123"
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.tv_card_title)
        val cardNumberPart1: TextView = itemView.findViewById(R.id.tv_card_number_part1)
        val cardNumberPart2: TextView = itemView.findViewById(R.id.tv_card_number_part2)
        val cardNumberPart3: TextView = itemView.findViewById(R.id.tv_card_number_part3)
        val cardNumberPart4: TextView = itemView.findViewById(R.id.tv_card_number_part4)
        val cardHolder: TextView = itemView.findViewById(R.id.tv_card_holder)
        val expireDate: TextView = itemView.findViewById(R.id.tv_expire_date)
        val cvv: TextView = itemView.findViewById(R.id.tv_cvv)
    }
}