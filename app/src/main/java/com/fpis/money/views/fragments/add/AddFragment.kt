package com.fpis.money.views.fragments.add

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
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
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fpis.money.R
import com.fpis.money.models.Card
import com.fpis.money.models.Category
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast
import com.fpis.money.views.fragments.add.category.CategoryBottomSheet
import com.fpis.money.views.fragments.add.category.CategoryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

    private var currentType = "expense"
    private var amount = "0"
    private var selectedCategory: String = ""

    private lateinit var amountValue: TextView
    private lateinit var tabExpense: LinearLayout
    private lateinit var tabIncome: LinearLayout
    private lateinit var tabTransfer: LinearLayout
    private lateinit var textExpense: TextView
    private lateinit var textIncome: TextView
    private lateinit var textTransfer: TextView
    private lateinit var addBtn: Button
    private lateinit var indicatorExpense: LinearLayout
    private lateinit var indicatorIncome: LinearLayout
    private lateinit var amountLabel: TextView
    private lateinit var categorySelection: LinearLayout
    private lateinit var categoryText: TextView
    private lateinit var categoryIcon: ImageView
    private lateinit var dateTimeValue: TextView
    private lateinit var notes: EditText
    private lateinit var accountNameTextView: TextView
    private lateinit var accountSelectionLayout: LinearLayout
    private var selectedPaymentMethod: String = "Select account"
    private var selectedSubcategory: String? = null
    private lateinit var subcategoriesContainer: NestedScrollView
    private lateinit var subcategoriesScrollView: HorizontalScrollView

    private var selectedIconRes: Int = R.drawable.ic_launcher_foreground
    private var selectedCategoryId: Int = -1

    private val iconMap = mapOf(
        "Food & Drink" to R.drawable.ic_food_drink,
        "Shopping" to R.drawable.ic_shopping,
        "Health" to R.drawable.health,
        "Transport" to R.drawable.ic_transport,
        "Interest" to R.drawable.ic_interest,
        "Life & Event" to R.drawable.ic_event,
        "Income" to R.drawable.ic_interest,
        "Salary" to R.drawable.ic_cards,
        "Bonus" to R.drawable.ic_debit_card,
        "Investment" to R.drawable.ic_cash,
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
        tabExpense = view.findViewById(R.id.expense_button_layout)
        tabIncome = view.findViewById(R.id.income_button_layout)
        tabTransfer = view.findViewById(R.id.transfer_button_layout)

        textExpense = view.findViewById(R.id.expense_button_text)
        textIncome = view.findViewById(R.id.income_button_text)
        textTransfer = view.findViewById(R.id.transfer_button_text)
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

        accountNameTextView = view.findViewById(R.id.account_name)
        accountSelectionLayout = view.findViewById(R.id.account_selection_layout)

        accountSelectionLayout.setOnClickListener {
            showAccountSelectionDialog()
        }

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

        parentFragmentManager.setFragmentResultListener("transaction_type_result", viewLifecycleOwner) { _, bundle ->
            val type = bundle.getString("transactionType", "expense")
            setType(type)
        }

        subcategoriesContainer = view.findViewById(R.id.subcategories_container)
        subcategoriesScrollView = view.findViewById(R.id.subcategories_scroll_view)

        val addButton = view.findViewById<LinearLayout>(R.id.add_subcategory_button)
        addButton.setOnClickListener {
            if (selectedCategory.isEmpty()) {
                showCustomToast(requireContext(), "Please select a category first", ToastType.INFO)
            } else {
                showAddSubcategoryDialog()
            }
        }
    }

    private fun saveTransaction() {
        if (selectedCategory.isEmpty()) {
            showCustomToast(requireContext(), "Please select a category", ToastType.INFO)
            return
        }

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

            addViewModel.saveTransaction(
                currentType,
                amount,
                selectedCategory,
                timestamp,
                notes.text.toString(),
                selectedPaymentMethod,
                selectedSubcategory ?: "",
                iconRes = selectedIconRes
            )

            showCustomToast(requireContext(), "Successfully saved transaction!", ToastType.SUCCESS)
            resetFields()
//            findNavController().navigate(R.id.action_addFragment_to_recordFragment)

        } catch (e: Exception) {
            Log.e("AddFragment", "Error saving transaction: ${e.message}")
            showCustomToast(requireContext(), "Error saving transaction", ToastType.ERROR)
        }
    }

    private fun resetFields() {
        amount = "0"
        currentType = "expense"
        selectedCategory = ""
        selectedSubcategory = null
        amountValue.text = "₸$amount"
        categoryText.text = "Select Category"
        categoryIcon.setImageResource(R.drawable.ic_launcher_foreground)
        dateTimeValue.text = "Select Date & Time"
        notes.setText("")
        selectedIconRes = R.drawable.ic_launcher_foreground

        val subcategoriesLinearLayout = subcategoriesContainer.findViewById<LinearLayout>(R.id.subcategories_linear_layout)
        subcategoriesLinearLayout.removeAllViews()

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
                textExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            "income" -> {
                amountLabel.text = "Income"
                indicatorIncome.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                textIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
            "transfer" -> {
                textTransfer.setTextColor(Color.WHITE)
            }
        }
    }

    private fun resetTabStyles() {
        textExpense.setTextColor(Color.WHITE)
        textIncome.setTextColor(Color.WHITE)
        textTransfer.setTextColor(Color.WHITE)
        indicatorIncome.setBackgroundColor(Color.parseColor("#0DEAECF0"))
        indicatorExpense.setBackgroundColor(Color.parseColor("#0DEAECF0"))
    }

    private fun openTransferFragment() {
        val transferFragment = TransferFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, transferFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showCategoryBottomSheet() {
        val isIncome = currentType == "income"
        val bottomSheet = CategoryBottomSheet(isIncome) { category ->
            selectedCategory = category.name
            selectedCategoryId = category.id
            categoryText.text = category.name
            categoryIcon.setImageResource(category.iconRes)
            selectedIconRes = category.iconRes

            loadSubcategoriesForCategory(category.id)
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

    private fun showAccountSelectionDialog() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val cardsRef = db.collection("users").document(userId).collection("cards")

        cardsRef.get()
            .addOnSuccessListener { documents ->
                val accounts = mutableListOf<String>()
                val cardMap = mutableMapOf<String, Card>()

                accounts.add("Cash")

                for (document in documents) {
                    val card = document.toObject(Card::class.java)
                    val lastFourDigits = if (card.cardNumber.length >= 4)
                        card.cardNumber.takeLast(4)
                    else
                        card.cardNumber
                    val displayName = "${card.bankName} •••• $lastFourDigits"
                    accounts.add(displayName)
                    cardMap[displayName] = card
                }

                AlertDialog.Builder(requireContext())
                    .setTitle("Select Account")
                    .setItems(accounts.toTypedArray()) { _, which ->
                        selectedPaymentMethod = accounts[which]
                        accountNameTextView.text = selectedPaymentMethod

                        val selectedCard = cardMap[selectedPaymentMethod]
                    }
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error fetching cards: ${e.message}")
                showCustomToast(requireContext(), "Failed to load accounts", ToastType.ERROR)
            }
    }

    private fun showAddSubcategoryDialog() {
        if (selectedCategoryId == -1) {
            showCustomToast(requireContext(), "Please select a category first", ToastType.INFO)
            return
        }

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_subcategory)

        val etName = dialog.findViewById<EditText>(R.id.etSubcategoryName)
        val btnSave = dialog.findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            if (name.isNotBlank()) {
                val viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
                viewModel.addCustomSubcategory(selectedCategoryId, name)

                addSubcategoryToView(name)
                dialog.dismiss()
            } else {
                etName.error = "Please enter subcategory name"
            }
        }

        dialog.show()
    }


    private fun loadSubcategoriesForCategory(categoryId: Int) {
        val viewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        viewModel.loadSubcategories(categoryId)
        viewModel.subcategories.observe(viewLifecycleOwner) { subcategories ->
            updateSubcategoriesList(subcategories.map { it.name })
        }
    }

    private fun updateSubcategoriesList(subcategoryNames: List<String>) {
        val subcategoriesLinearLayout = subcategoriesContainer.findViewById<LinearLayout>(R.id.subcategories_linear_layout)
        subcategoriesLinearLayout.removeAllViews()

        val addButton = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_add_subcategory, subcategoriesLinearLayout, false)
        addButton.setOnClickListener { showAddSubcategoryDialog() }
        subcategoriesLinearLayout.addView(addButton)

        subcategoryNames.forEach { name ->
            addSubcategoryToView(name)
        }
    }

    private fun addSubcategoryToView(name: String) {
        val subcategoriesLinearLayout = subcategoriesContainer.findViewById<LinearLayout>(R.id.subcategories_linear_layout)

        subcategoriesLinearLayout.removeViewAt(subcategoriesLinearLayout.childCount - 1)

        val subcategoryView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_subcategory, subcategoriesLinearLayout, false)

        subcategoryView.findViewById<TextView>(R.id.subcategory_label).text = name
        subcategoryView.findViewById<ImageView>(R.id.subcategory_icon).setImageResource(
            iconMap[selectedCategory] ?: R.drawable.ic_launcher_foreground
        )

        subcategoryView.setOnClickListener {
            selectedSubcategory = name
            for (i in 0 until subcategoriesLinearLayout.childCount) {
                val child = subcategoriesLinearLayout.getChildAt(i)
                child.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_subcategory)
            }
            subcategoryView.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_subcategory_selected)
        }

        subcategoriesLinearLayout.addView(subcategoryView)

        val addButton = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_add_subcategory, subcategoriesLinearLayout, false)
        addButton.setOnClickListener { showAddSubcategoryDialog() }
        subcategoriesLinearLayout.addView(addButton)
    }
}