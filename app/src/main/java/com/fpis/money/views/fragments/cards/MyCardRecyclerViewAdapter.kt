package com.fpis.money.views.fragments.cards

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.fpis.money.R
import com.fpis.money.models.Card
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast

class MyCardRecyclerViewAdapter(
    private var cardsList: MutableList<Card>,
    private val onEditCard: (Int) -> Unit
) : RecyclerView.Adapter<MyCardRecyclerViewAdapter.ViewHolder>() {

    private val visibilityMap = mutableMapOf<Int, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_card, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cardsList[position]
        val isVisible = visibilityMap[position] ?: false

        holder.cardTitle.text = if (card.cardType == "Credit") "My Credit Card" else "My Debit Card"
        val colorInt = Color.parseColor(card.cardColor)
        holder.cardContent.setCardBackgroundColor(colorInt)

        val cleanNumber = card.cardNumber.padEnd(16, '*')  // just in case
        holder.cardNumber1.text = if (isVisible) cleanNumber.substring(0, 4) else "* * * *"
        holder.cardNumber2.text = if (isVisible) cleanNumber.substring(4, 8) else "* * * *"
        holder.cardNumber3.text = if (isVisible) cleanNumber.substring(8, 12) else "* * * *"
        holder.cardNumber4.text = cleanNumber.substring(12, 16)

        holder.cardHolder.text = card.cardHolder
        holder.expireDate.text = if (isVisible) card.expiryDate else "**/**"
        holder.cvv.text = "CVV: ${if (isVisible) card.cvv else "***"}"

        holder.visibilityIcon.setImageResource(
            if (isVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
        )

        holder.visibilityIcon.setOnClickListener {
            visibilityMap[position] = !(visibilityMap[position] ?: false)
            notifyItemChanged(position)
        }

        holder.moreIcon.setOnClickListener {
            showCardOptionsDialog(holder.itemView, position)
        }
    }

    fun updateCards(newCards: List<Card>) {
        cardsList = newCards.toMutableList()
        notifyDataSetChanged()
    }

    private fun showCardOptionsDialog(view: View, position: Int) {
        val context = view.context

        // Create and show the custom dialog
        CardOption(
            context,
            onEditClick = {
                // Handle edit card
                onEditCard(position)
            },
            onDeleteClick = {
                // Show confirmation dialog before deleting
                showDeleteConfirmationDialog(context, position)
            }
        ).show()
    }

    private fun showDeleteConfirmationDialog(context: android.content.Context, position: Int) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.card_delete_conf)

        // Set up dialog window properties
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            attributes.windowAnimations = R.style.DialogAnimation
        }

        // Set up click listeners
        val cancelButton = dialog.findViewById<Button>(R.id.btn_cancel)
        val deleteButton = dialog.findViewById<Button>(R.id.btn_delete)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        deleteButton.setOnClickListener {
            // Delete the card
            if (position < cardsList.size) {
                cardsList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, cardsList.size)
                showCustomToast(context, "Card deleted", ToastType.SUCCESS)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun getItemCount(): Int = cardsList.size

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
