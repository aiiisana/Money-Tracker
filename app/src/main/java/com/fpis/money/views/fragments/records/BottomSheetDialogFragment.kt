package com.fpis.money.views.fragments.records

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.fpis.money.R
import com.fpis.money.models.Transaction
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class RecordDetailBottomSheet(private val transaction: Transaction) : BottomSheetDialogFragment() {

    private lateinit var currentType: String
    private lateinit var amountValue: TextView
    private lateinit var categoryText: TextView
    private lateinit var categoryIcon: ImageView
    private lateinit var indicatorExpense: View
    private lateinit var indicatorIncome: View
    private lateinit var indicatorTransfer: View
    private lateinit var tabExpense: TextView
    private lateinit var tabIncome: TextView
    private lateinit var tabTransfer: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record_detail, container, false)

        val accountName = view.findViewById<TextView>(R.id.account_name)
        categoryText = view.findViewById(R.id.category_text) // Инициализация categoryText
        val subCategory = view.findViewById<TextView>(R.id.subcategory_label)
        amountValue = view.findViewById(R.id.amount_value)
        val date = view.findViewById<TextView>(R.id.date_time_value)
        val notes = view.findViewById<TextView>(R.id.notes_input)

        // UI elements for tab selection
        indicatorExpense = view.findViewById(R.id.expense_button_indicator)
        indicatorIncome = view.findViewById(R.id.income_button_indicator)
        indicatorTransfer = view.findViewById(R.id.transfer_button_indicator)
        tabExpense = view.findViewById(R.id.expense_button_text)
        tabIncome = view.findViewById(R.id.income_button_text)
        tabTransfer = view.findViewById(R.id.transfer_button_text)

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(transaction.date)

        accountName.text = transaction.paymentMethod
        categoryText.text = transaction.category // Отображение категории
        subCategory.text = transaction.subCategory
        amountValue.text = formatAmount(transaction.amount, transaction.type)
        date.text = formattedDate
        notes.text = transaction.notes

        setType(transaction.type)  // Set the type for initial display
        setTabClickListeners()     // Set listeners to change type

        return view
    }

    private fun formatAmount(amount: Float, type: String): String {
        return if (type == "income") "₸$amount" else "-₸$amount"
    }

    private fun setType(type: String) {
        currentType = type
        updateAmountValue()
        updateTabSelection()
    }

    private fun updateAmountValue() {
        when (currentType) {
            "expense" -> {
                amountValue.text = "-₸${transaction.amount}"
                amountValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            "income" -> {
                amountValue.text = "₸${transaction.amount}"
                amountValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
            "transfer" -> {
                amountValue.text = "₸${transaction.amount}"
                amountValue.setTextColor(Color.WHITE)
            }
        }
    }

    private fun updateTabSelection() {
        resetTabStyles()

        when (currentType) {
            "expense" -> {
                indicatorExpense.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                tabExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            "income" -> {
                indicatorIncome.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                tabIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
            "transfer" -> {
                tabTransfer.setTextColor(Color.WHITE)
                indicatorTransfer.setBackgroundColor(Color.parseColor("#0DEAECF0"))
            }
        }
    }

    private fun resetTabStyles() {
        tabExpense.setTextColor(Color.WHITE)
        tabIncome.setTextColor(Color.WHITE)
        tabTransfer.setTextColor(Color.WHITE)
        indicatorIncome.setBackgroundColor(Color.parseColor("#0DEAECF0"))
        indicatorExpense.setBackgroundColor(Color.parseColor("#0DEAECF0"))
        indicatorTransfer.setBackgroundColor(Color.parseColor("#0DEAECF0"))
    }

    private fun setTabClickListeners() {
        tabExpense.setOnClickListener {
            setType("expense")
        }
        tabIncome.setOnClickListener {
            setType("income")
        }
        tabTransfer.setOnClickListener {
            setType("transfer")
        }
    }
}