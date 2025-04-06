package com.fpis.money.views.fragments.add

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fpis.money.R

class AddFragment : Fragment() {

    private var currentType = "expense"
    private var amount = "0"

    private lateinit var amountValue: TextView
    private lateinit var tabExpense: TextView
    private lateinit var tabIncome: TextView
    private lateinit var tabTransfer: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        amountValue = view.findViewById(R.id.amount_value)
        tabExpense = view.findViewById(R.id.tab_expense)
        tabIncome = view.findViewById(R.id.tab_income)
        tabTransfer = view.findViewById(R.id.tab_transfer)

        // Set initial state
        updateAmountValue()
        updateTabSelection()

        // Set click listeners
        amountValue.setOnClickListener { showAmountInputBottomSheet() }
        tabExpense.setOnClickListener { setType("expense") }
        tabIncome.setOnClickListener { setType("income") }
        tabTransfer.setOnClickListener { setType("transfer") }
    }

    private fun showAmountInputBottomSheet() {
        val bottomSheet = AmountInputBottomSheet { enteredAmount ->
            amount = enteredAmount
            updateAmountValue()
        }
        bottomSheet.show(parentFragmentManager, "AmountInputBottomSheet")
    }

    private fun setType(type: String) {
        currentType = type
        updateAmountValue()
        updateTabSelection()
    }

    private fun updateAmountValue() {
        when (currentType) {
            "expense" -> {
                amountValue.text = "-₸$amount"
                amountValue.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.red)
                )
            }
            "income" -> {
                amountValue.text = "₸$amount"
                amountValue.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.green)
                )
            }
            "transfer" -> {
                amountValue.text = "₸$amount"
                amountValue.setTextColor(Color.WHITE)
            }
        }
    }

    private fun updateTabSelection() {
        resetTabStyles()

        when (currentType) {
            "expense" -> {
                tabExpense.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.red)
                )
            }
            "income" -> {
                tabIncome.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.green)
                )
            }
            "transfer" -> {
                tabTransfer.setTextColor(Color.WHITE)
            }
        }
    }

    private fun resetTabStyles() {
        tabExpense.setTextColor(Color.WHITE)
        tabIncome.setTextColor(Color.WHITE)
        tabTransfer.setTextColor(Color.WHITE)
    }
}
