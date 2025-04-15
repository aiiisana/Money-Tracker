package com.fpis.money.views.fragments.records

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fpis.money.R
import com.fpis.money.models.Transaction
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast
import com.fpis.money.views.fragments.add.AmountInputBottomSheet
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class RecordDetailBottomSheet(private val transaction: Transaction) : BottomSheetDialogFragment() {

    private lateinit var detailsViewModel: DetailsViewModel
    private lateinit var amountValue: TextView
    private lateinit var dateTimeValue: TextView
    private lateinit var notesInput: EditText
    private lateinit var categoryText: TextView
    private lateinit var categoryIcon: ImageView
    private lateinit var subcategoryText: TextView
    private lateinit var saveButton: Button
    private lateinit var expenseText: TextView
    private lateinit var incomeText: TextView
    private lateinit var expenseIndicator: LinearLayout
    private lateinit var incomeIndicator: LinearLayout
    private var amount: Float = transaction.amount

    private val iconMap = mapOf(
        "Food & Drink" to R.drawable.ic_food_drink,
        "Shopping" to R.drawable.ic_shopping,
        "Health" to R.drawable.health,
        "Transport" to R.drawable.ic_transport,
        "Interest" to R.drawable.ic_interest,
        "Life & Event" to R.drawable.ic_event,
        "Income" to R.drawable.ic_interest
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        detailsViewModel = ViewModelProvider(this, DetailsViewViewModelFactory(requireActivity().application))
            .get(DetailsViewModel::class.java)

        return inflater.inflate(R.layout.fragment_record_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amountValue = view.findViewById(R.id.amount_value)
        dateTimeValue = view.findViewById(R.id.date_time_value)
        notesInput = view.findViewById(R.id.notes_input)
        categoryText = view.findViewById(R.id.category_text)
        categoryIcon = view.findViewById(R.id.category_icon)
        subcategoryText = view.findViewById(R.id.subcategory_label)
        saveButton = view.findViewById(R.id.record_save_button)

        expenseText = view.findViewById(R.id.expense_button_text)
        incomeText = view.findViewById(R.id.income_button_text)
        expenseIndicator = view.findViewById(R.id.expense_button_indicator)
        incomeIndicator = view.findViewById(R.id.income_button_indicator)

        fillTransactionData()

        amountValue.setOnClickListener {
            showAmountInputBottomSheet()
        }

        setupListeners()
    }

    private fun fillTransactionData() {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(transaction.date))

        amountValue.text = formatAmount(transaction.amount, transaction.type)
        dateTimeValue.text = formattedDate
        notesInput.setText(transaction.notes)
        categoryText.text = transaction.category
        subcategoryText.text = transaction.subCategory

        transaction.iconRes?.let {
            categoryIcon.setImageResource(it)
        } ?: run {
            iconMap[transaction.category]?.let {
                categoryIcon.setImageResource(it)
            }
        }

        when (transaction.type) {
            "income" -> {
                amountValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                incomeText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                incomeIndicator.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                expenseText.setTextColor(Color.WHITE)
                expenseIndicator.setBackgroundColor(Color.parseColor("#0DEAECF0"))
            }
            "expense" -> {
                amountValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                expenseText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                expenseIndicator.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                incomeText.setTextColor(Color.WHITE)
                incomeIndicator.setBackgroundColor(Color.parseColor("#0DEAECF0"))
            }
            else -> {
                amountValue.setTextColor(Color.WHITE)
                incomeText.setTextColor(Color.WHITE)
                expenseText.setTextColor(Color.WHITE)
                incomeIndicator.setBackgroundColor(Color.parseColor("#0DEAECF0"))
                expenseIndicator.setBackgroundColor(Color.parseColor("#0DEAECF0"))
            }
        }
    }

    private fun formatAmount(amount: Float, type: String): String {
        return if (type == "income") "₸$amount" else "-₸$amount"
    }

    private fun setupListeners() {
        saveButton.setOnClickListener {
            saveUpdatedTransaction()
        }

        view?.findViewById<View>(R.id.date_button)?.setOnClickListener {
            showDateTimePicker()
        }

        view?.findViewById<ImageView>(R.id.close_record_detail_button)?.setOnClickListener {
            dismiss()
        }
    }

    private fun saveUpdatedTransaction() {
        val updatedAmount = parseAmount(amountValue.text.toString())
        val updatedDate = parseDateTime(dateTimeValue.text.toString())
        val updatedNotes = notesInput.text.toString()

        if (updatedAmount == null) {
            showCustomToast(requireContext(), "Invalid amount", ToastType.ERROR)
            return
        }

        val updatedTransaction = transaction.copy(
            amount = updatedAmount,
            date = updatedDate,
            notes = updatedNotes
        )

        detailsViewModel.updateTransaction(updatedTransaction).observe(viewLifecycleOwner) { success ->
            if (success) {
                showCustomToast(requireContext(), "Transaction updated", ToastType.SUCCESS)
                dismiss()
            } else {
                showCustomToast(requireContext(), "Failed to update transaction", ToastType.ERROR)
            }
        }
    }

    private fun parseAmount(amountString: String): Float? {
        return try {
            amountString.replace("₸", "").replace("-", "").toFloat()
        } catch (e: Exception) {
            null
        }
    }

    private fun parseDateTime(dateTimeString: String): Long {
        return try {
            val format = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            format.parse(dateTimeString)?.time ?: transaction.date
        } catch (e: Exception) {
            transaction.date
        }
    }


    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = transaction.date
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        val selectedDateTime = Calendar.getInstance().apply {
                            set(year, month, day, hour, minute)
                        }

                        val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                            .format(selectedDateTime.time)

                        dateTimeValue.text = formattedDate
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateAmountValue() {
        amountValue.text = formatAmount(amount, transaction.type)
    }

    private fun showAmountInputBottomSheet() {
        val bottomSheet = AmountInputBottomSheet { enteredAmount ->
            amount = enteredAmount.replace(",", ".").toFloatOrNull() ?: 0f
            updateAmountValue()
        }
        bottomSheet.show(parentFragmentManager, "AmountInputBottomSheet")
    }
}