package com.fpis.money.views.fragments.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fpis.money.R
import com.fpis.money.views.fragments.records.RecordFragment
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

    private var currentType = "expense"
    private var amount = "0"
    private lateinit var selectedCategory: String

    private lateinit var amountValue: TextView
    private lateinit var tabExpense: TextView
    private lateinit var tabIncome: TextView
    private lateinit var tabTransfer: TextView
    private lateinit var addBtn: Button
    private lateinit var indicatorExpense: LinearLayout
    private lateinit var indicatorIncome: LinearLayout
    private lateinit var amountLabel: TextView
    private lateinit var categorySelection: LinearLayout
    private lateinit var categoryText: TextView
    private lateinit var categoryIcon: ImageView
    private lateinit var dateTimeValue: TextView
    private lateinit var notes: EditText

    private val iconMap = mapOf(
        "Food & Drink" to R.drawable.ic_food_drink,
        "Shopping" to R.drawable.ic_shopping,
        "Health" to R.drawable.health,
        "Transport" to R.drawable.ic_transport,
        "Interest" to R.drawable.ic_interest,
        "Life & Event" to R.drawable.ic_event,
        "Income" to R.drawable.ic_interest,
        "Add New Category" to R.drawable.ic_add
    )

    private lateinit var addViewModel: AddViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addViewModel = ViewModelProvider(this, AddViewModelFactory(requireActivity().application)).get(AddViewModel::class.java)
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amountValue = view.findViewById(R.id.amount_value)
        tabExpense = view.findViewById(R.id.expense_button_text)
        tabIncome = view.findViewById(R.id.income_button_text)
        tabTransfer = view.findViewById(R.id.transfer_button_text)
        addBtn = view.findViewById(R.id.add_record_button)

        indicatorIncome = view.findViewById(R.id.income_button_indicator)
        indicatorExpense = view.findViewById(R.id.expense_button_indicator)

        amountLabel = view.findViewById(R.id.amount_label)

        categorySelection = view.findViewById(R.id.category_selection_layout)
        categoryText = view.findViewById(R.id.category_name)
        categoryIcon = view.findViewById(R.id.category_icon)

        val dateTimeSelectionLayout = view.findViewById<LinearLayout>(R.id.date_time_selection_layout)
        dateTimeValue = view.findViewById(R.id.date_time_value)

        notes = view.findViewById(R.id.notes_input)

        updateAmountValue()
        updateTabSelection()

        amountValue.setOnClickListener { showAmountInputBottomSheet() }
        tabExpense.setOnClickListener { setType("expense") }
        tabIncome.setOnClickListener { setType("income") }
        tabTransfer.setOnClickListener { openTransferFragment() }
        addBtn.setOnClickListener { saveTransaction() }
        categorySelection.setOnClickListener { showCategoryBottomSheet() }

        dateTimeSelectionLayout.setOnClickListener {
            showDateTimePicker()
        }
    }

    private fun saveTransaction() {
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)

        val dateTimeString = dateTimeValue.text.toString()
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

        try {
            val date: Date? = formatter.parse(dateTimeString)
            val timestamp: Long = date?.time ?: 0L

            if (timestamp == 0L) {
                throw Exception("Invalid date")
            }

            addViewModel.saveTransaction(currentType, amount, selectedCategory, timestamp, notes.text.toString())
            resetFields()

            val navController = findNavController()
            navController.navigate(R.id.action_addFragment_to_recordFragment)

        } catch (e: Exception) {
            Log.e("AddFragment", "Error parsing date: ${e.message}")
        }
    }

    private fun resetFields() {
        amount = "0"
        currentType = "expense"
        selectedCategory = ""
        amountValue.text = "₸$amount"
        categoryText.text = "Select Category"
        categoryIcon.setImageResource(R.drawable.ic_launcher_foreground)
        dateTimeValue.text = "Select Date & Time"
        updateAmountValue()
        updateTabSelection()
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

        if (type == "income") {
            selectedCategory = "Income"
            categoryText.text = selectedCategory
            categoryIcon.setImageResource(iconMap[selectedCategory] ?: R.drawable.ic_launcher_foreground)
        }
    }

    private fun updateAmountValue() {
        when (currentType) {
            "expense" -> {
                amountValue.text = "-₸$amount"
                amountValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            "income" -> {
                amountValue.text = "₸$amount"
                amountValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
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
                amountLabel.text = "Expense"
                indicatorExpense.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                tabExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            "income" -> {
                amountLabel.text = "Income"
                indicatorIncome.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                tabIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
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
        indicatorIncome.setBackgroundColor(Color.parseColor("#0DEAECF0"))
        indicatorExpense.setBackgroundColor(Color.parseColor("#0DEAECF0"))
    }

    private fun openTransferFragment() {
        val transferFragment = TransferFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, transferFragment)
            .commit()  // без addToBackStack
    }

    private fun showCategoryBottomSheet() {
        val categories = if (currentType == "income") {
            listOf("Income")
        } else {
            listOf(
                "Food & Drink",
                "Shopping",
                "Health",
                "Transport",
                "Interest",
                "Life & Event",
                "Add New Category"
            )
        }

        val bottomSheet = CategoryBottomSheet(categories) { category ->
            selectedCategory = category
            categoryText.text = category
            categoryIcon.setImageResource(iconMap[category] ?: R.drawable.ic_launcher_foreground)
        }

        bottomSheet.show(parentFragmentManager, "CategoryBottomSheet")
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, selectedHour, selectedMinute ->
                        val selectedDateTime = String.format(
                            "%02d %s %04d, %02d:%02d %s",
                            selectedDay,
                            getMonthName(selectedMonth),
                            selectedYear,
                            if (selectedHour > 12) selectedHour - 12 else selectedHour,
                            selectedMinute,
                            if (selectedHour >= 12) "PM" else "AM"
                        )
                        dateTimeValue.text = selectedDateTime
                    },
                    hour,
                    minute,
                    false
                )
                timePickerDialog.show()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun getMonthName(month: Int): String {
        val months = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        return months[month]
    }
}