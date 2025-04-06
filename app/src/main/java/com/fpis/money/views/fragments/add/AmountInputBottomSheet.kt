package com.fpis.money.views.fragments.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.fpis.money.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AmountInputBottomSheet(private val onAmountEntered: (String) -> Unit) :
    BottomSheetDialogFragment() {

    private lateinit var amountTextView: TextView
    private var amount: StringBuilder = StringBuilder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_amount_input, container, false)

        amountTextView = view.findViewById(R.id.tv_amount)

        val buttons = listOf(
            R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
            R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9
        )

        for (buttonId in buttons) {
            view.findViewById<Button>(buttonId).setOnClickListener {
                val digit = (it as Button).text.toString()
                amount.append(digit)
                updateAmount()
            }
        }

        view.findViewById<Button>(R.id.btn_dot).setOnClickListener {
            if (!amount.contains(".")) {
                amount.append(".")
                updateAmount()
            }
        }

        view.findViewById<Button>(R.id.btn_delete).setOnClickListener {
            if (amount.isNotEmpty()) {
                amount.deleteCharAt(amount.length - 1)
                updateAmount()
            }
        }

        view.findViewById<Button>(R.id.btn_save).setOnClickListener {
            onAmountEntered(amount.toString())
            dismiss()
        }

        return view
    }

    private fun updateAmount() {
        amountTextView.text = "-₸$amount"
    }
}