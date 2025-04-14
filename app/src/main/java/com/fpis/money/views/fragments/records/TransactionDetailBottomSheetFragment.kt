package com.fpis.money.views.fragments.records

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.fpis.money.R
import com.fpis.money.models.Transaction
import com.fpis.money.models.Transfer
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionDetailBottomSheetFragment(private val transfer: Transfer) : BottomSheetDialogFragment() {

    private lateinit var currentType: String
    private lateinit var amountText: TextView
    private lateinit var expenseLine: View
    private lateinit var incomeLine: View
    private lateinit var transferLine: View
    private lateinit var expenseLabel: TextView
    private lateinit var incomeLabel: TextView
    private lateinit var transferLabel: TextView
    private lateinit var fromAccountName: TextView
    private lateinit var toAccountName: TextView
    private lateinit var dateTimeValue: TextView
    private lateinit var notesInput: EditText
    private lateinit var addRecordButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_detail_bottom_sheet, container, false)

        amountText = view.findViewById(R.id.amountText)
        expenseLine = view.findViewById(R.id.expenseLine)
        incomeLine = view.findViewById(R.id.incomeLine)
        transferLine = view.findViewById(R.id.transferLine)
        expenseLabel = view.findViewById(R.id.expenseLabel)
        incomeLabel = view.findViewById(R.id.incomeLabel)
        transferLabel = view.findViewById(R.id.transferLabel)
        fromAccountName = view.findViewById(R.id.fromAccountName)
        toAccountName = view.findViewById(R.id.toAccountName)
        dateTimeValue = view.findViewById(R.id.date_time_value)
        notesInput = view.findViewById(R.id.notes_input)
        addRecordButton = view.findViewById(R.id.add_record_button)

        currentType = "transfer"
        updateTypeSelection()

        expenseLabel.setOnClickListener { setType("expense") }
        incomeLabel.setOnClickListener { setType("income") }
        transferLabel.setOnClickListener { setType("transfer") }

        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        dateTimeValue.text = dateFormat.format(transfer.date)

        addRecordButton.setOnClickListener {
            saveRecord()
        }

        amountText.text = "₸${transfer.amount}"
        fromAccountName.text = transfer.fromAccount
        toAccountName.text = transfer.toAccount
        notesInput.setText(transfer.notes)

        return view
    }

    private fun setType(type: String) {
        currentType = type
        updateTypeSelection()
        updateAmountColor()
    }

    private fun updateTypeSelection() {
        expenseLine.setBackgroundColor(Color.parseColor("#0DFFFFFF"))
        incomeLine.setBackgroundColor(Color.parseColor("#0DFFFFFF"))
        transferLine.setBackgroundColor(Color.parseColor("#0DFFFFFF"))
        expenseLabel.setTextColor(Color.WHITE)
        incomeLabel.setTextColor(Color.WHITE)
        transferLabel.setTextColor(Color.WHITE)

        when (currentType) {
            "expense" -> {
                expenseLine.setBackgroundColor(Color.RED)
                expenseLabel.setTextColor(Color.RED)
            }
            "income" -> {
                incomeLine.setBackgroundColor(Color.GREEN)
                incomeLabel.setTextColor(Color.GREEN)
            }
            "transfer" -> {
                transferLine.setBackgroundColor(Color.WHITE)
                transferLabel.setTextColor(Color.WHITE)
            }
        }
    }

    private fun updateAmountColor() {
        when (currentType) {
            "expense" -> amountText.setTextColor(Color.RED)
            "income" -> amountText.setTextColor(Color.GREEN)
            "transfer" -> amountText.setTextColor(Color.WHITE)
        }
    }

    private fun saveRecord() {
        val amount = amountText.text.toString()
        val fromAccount = fromAccountName.text.toString()
        val toAccount = toAccountName.text.toString()
        val dateTime = dateTimeValue.text.toString()
        val notes = notesInput.text.toString()

        if (validateInput()) {
            dismiss()
        }
    }

    private fun validateInput(): Boolean {
        if (amountText.text == "₸0") {
            context?.let { showCustomToast(it, "Please enter amount", ToastType.INFO) }
            return false
        }

        if (currentType == "transfer" && (fromAccountName.text == "Select an account" ||
                    toAccountName.text == "Select an account")) {
            context?.let { showCustomToast(it, "Please select both accounts", ToastType.INFO) }
            return false
        }

        return true
    }
}