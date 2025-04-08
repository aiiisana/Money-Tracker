package com.fpis.money.views.fragments.cards

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.fpis.money.R

import com.fpis.money.views.fragments.cards.placeholder.PlaceholderContent.PlaceholderItem

class MyCardRecyclerViewAdapter(
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<MyCardRecyclerViewAdapter.ViewHolder>() {

    // Map to store visibility state for each card position
    private val visibilityMap = mutableMapOf<Int, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use the new item_card.xml layout
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_card, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        // Get current visibility state (default to false/hidden)
        val isVisible = visibilityMap[position] ?: false

        // Set card title based on position
        if (position % 2 == 0) {
            holder.cardTitle.text = "My Credit Card"
            holder.cardContent.setCardBackgroundColor(holder.itemView.context.getColor(R.color.credit_card_blue))
        } else {
            holder.cardTitle.text = "My Debit Card"
            holder.cardContent.setCardBackgroundColor(holder.itemView.context.getColor(R.color.debit_card_orange))
        }

        // Set card details based on visibility
        holder.cardNumber1.text = if (isVisible) "1234" else "* * * *"
        holder.cardNumber2.text = if (isVisible) "5678" else "* * * *"
        holder.cardNumber3.text = if (isVisible) "9012" else "* * * *"
        holder.cardNumber4.text = "1234" // Last 4 digits always visible

        holder.cardHolder.text = "John Doe"
        holder.expireDate.text = if (isVisible) "11/25" else "**/**"
        holder.cvv.text = "CVV: ${if (isVisible) "123" else "***"}"

        // Set visibility icon based on current state
        holder.visibilityIcon.setImageResource(
            if (isVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
        )

        // Set click listeners
        holder.visibilityIcon.setOnClickListener {
            // Toggle card number visibility
            val newVisibility = !(visibilityMap[position] ?: false)
            visibilityMap[position] = newVisibility
            notifyItemChanged(position)
        }

        holder.moreIcon.setOnClickListener {
            // Show options menu
        }
        // Set up add button click listener
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