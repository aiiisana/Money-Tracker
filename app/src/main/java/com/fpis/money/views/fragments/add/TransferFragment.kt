package com.fpis.money.views.fragments.add

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import com.fpis.money.models.Card
import com.fpis.money.models.Transfer
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class TransferFragment : Fragment() {

    private val addViewModel: AddViewModel by activityViewModels()

    private var fromAccount: String? = null
    private var toAccount: String? = null
    private var amount = "0"

    private lateinit var amountTextView: TextView
    private lateinit var noteEditText: EditText
    private lateinit var fromAccountTextView: TextView
    private lateinit var toAccountTextView: TextView
    private lateinit var saveTransferButton: Button
    private lateinit var dateTimeTextView: TextView
    private var selectedTimestamp: Long = System.currentTimeMillis()

    private lateinit var favoritesRecycler: RecyclerView
    private lateinit var favoritesAdapter: FavoriteTransfersAdapter
    private lateinit var saveAsFavoriteButton: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transfer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expenseOption = view.findViewById<LinearLayout>(R.id.expenseOption)
        val incomeOption = view.findViewById<LinearLayout>(R.id.incomeOption)

        val fromAccountLayout = view.findViewById<LinearLayout>(R.id.fromAccountText)
        val toAccountLayout = view.findViewById<LinearLayout>(R.id.toAccountText)

        fromAccountTextView = view.findViewById(R.id.fromAccountName)
        toAccountTextView = view.findViewById(R.id.toAccountName)
        amountTextView = view.findViewById(R.id.amountText)
        noteEditText = view.findViewById(R.id.notes_input)
        saveTransferButton = view.findViewById(R.id.add_record_button)

        dateTimeTextView = view.findViewById(R.id.date_time_value)
        updateDateTimeText(selectedTimestamp)

        dateTimeTextView.setOnClickListener {
            showDateTimePicker()
        }

        expenseOption.setOnClickListener {
            navigateToAddFragment("expense")
        }

        incomeOption.setOnClickListener {
            navigateToAddFragment("income")
        }

        fromAccountLayout.setOnClickListener {
            showAccountSelection { selected ->
                fromAccount = selected
                fromAccountTextView.text = selected
            }
        }

        toAccountLayout.setOnClickListener {
            showAccountSelection { selected ->
                toAccount = selected
                toAccountTextView.text = selected
            }
        }

        amountTextView.setOnClickListener {
            showAmountInputBottomSheet()
        }

        saveTransferButton.setOnClickListener {
            val parsedAmount = amount.toFloatOrNull()
            val note = noteEditText.text.toString()
            val timestamp = selectedTimestamp

            if (parsedAmount == null || parsedAmount <= 0f) {
                showCustomToast(requireContext(), "Enter a valid amount", ToastType.INFO)
                return@setOnClickListener
            }

            if (fromAccount.isNullOrBlank() || toAccount.isNullOrBlank()) {
                showCustomToast(requireContext(), "Select both accounts", ToastType.INFO)
                return@setOnClickListener
            }

            if (fromAccount == toAccount) {
                showCustomToast(requireContext(), "Cannot transfer to the same account", ToastType.INFO)
                return@setOnClickListener
            }

            addViewModel.saveTransfer(
                fromAccount = fromAccount!!,
                toAccount = toAccount!!,
                amount = parsedAmount,
                date = timestamp,
                notes = note
            )

            showCustomToast(requireContext(), "Transfer saved", ToastType.SUCCESS)
            resetAllFields()
            parentFragmentManager.popBackStack()
        }

        favoritesRecycler = view.findViewById(R.id.favorites_recycler)
        favoritesAdapter = FavoriteTransfersAdapter(
            { favorite -> applyFavoriteTransfer(favorite) },
            { favorite -> showDeleteFavoriteDialog(favorite) }
        )
        favoritesRecycler.adapter = favoritesAdapter

        loadFavoriteTransfers()

        val buttonLayout = view.findViewById<LinearLayout>(R.id.add_record_button_layout)
        buttonLayout.orientation = LinearLayout.HORIZONTAL

        saveAsFavoriteButton = Button(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginEnd = 8.dpToPx()
            }
            text = "Save as Favorite"
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            background = ContextCompat.getDrawable(requireContext(), R.drawable.button_background_secondary)
            setOnClickListener {
                saveCurrentAsFavorite()
            }
        }

        buttonLayout.addView(saveAsFavoriteButton, 0)
    }

    private fun showDeleteFavoriteDialog(favorite: Transfer) {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Favorite")
                .setMessage("Are you sure you want to delete this favorite transaction?")
                .setPositiveButton("Delete") { _, _ ->
                    addViewModel.deleteFavoriteTransfer(favorite)
                    showCustomToast(requireContext(), "Favorite deleted", ToastType.SUCCESS)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun loadFavoriteTransfers() {
        addViewModel.getFavoriteTransfers().observe(viewLifecycleOwner) { favorites ->
            favoritesAdapter.submitList(favorites)
        }
    }

    private fun applyFavoriteTransfer(favorite: Transfer) {
        fromAccount = favorite.fromAccount
        toAccount = favorite.toAccount
        amount = favorite.amount.toString()
        noteEditText.setText(favorite.notes)

        fromAccountTextView.text = favorite.fromAccount
        toAccountTextView.text = favorite.toAccount
        amountTextView.text = "₸${favorite.amount}"

        selectedTimestamp = System.currentTimeMillis()
        updateDateTimeText(selectedTimestamp)

        showCustomToast(requireContext(), "Favorite applied! Review and confirm", ToastType.INFO)
    }

    private fun saveCurrentAsFavorite() {
        val parsedAmount = amount.toFloatOrNull()
        val timestamp = selectedTimestamp

        if (fromAccount.isNullOrBlank() || toAccount.isNullOrBlank()) {
            showCustomToast(requireContext(), "Select both accounts", ToastType.INFO)
            return
        }

        if (parsedAmount == null || parsedAmount <= 0) {
            showCustomToast(requireContext(), "Enter a valid amount", ToastType.INFO)
            return
        }

        if (fromAccount == toAccount) {
            showCustomToast(requireContext(), "Cannot save transfer to the same account", ToastType.INFO)
            return
        }

        val transfer = Transfer(
            fromAccount = fromAccount!!,
            toAccount = toAccount!!,
            amount = parsedAmount,
            date = timestamp,
            notes = noteEditText.text.toString(),
            isFavorite = true
        )

        addViewModel.saveAsFavoriteTransfer(transfer)
        showCustomToast(requireContext(), "Transfer saved as favorite!", ToastType.SUCCESS)
    }

    private fun navigateToAddFragment(type: String) {
        parentFragmentManager.setFragmentResult("transaction_type_result", Bundle().apply {
            putString("transactionType", type)
        })
        parentFragmentManager.popBackStack()
    }

    private fun showAccountSelection(onSelected: (String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val cardsRef = db.collection("users").document(userId).collection("cards")

        cardsRef.get()
            .addOnSuccessListener { documents ->
                val accounts = mutableListOf<String>()
                accounts.add("Cash")

                for (doc in documents) {
                    val card = doc.toObject(Card::class.java)
                    val last4 = card.cardNumber.takeLast(4)
                    accounts.add("${card.bankName} •••• $last4")
                }

                AlertDialog.Builder(requireContext())
                    .setTitle("Choose an account")
                    .setItems(accounts.toTypedArray()) { _, which ->
                        onSelected(accounts[which])
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            .addOnFailureListener {
                showCustomToast(requireContext(), "Failed to load accounts", ToastType.ERROR)
            }
    }

    private fun showAmountInputBottomSheet() {
        val bottomSheet = AmountInputBottomSheet { enteredAmount ->
            amount = enteredAmount
            updateAmountText()
        }
        bottomSheet.show(parentFragmentManager, "AmountInputBottomSheet")
    }

    private fun updateAmountText() {
        amountTextView.text = "₸$amount"
        amountTextView.setTextColor(Color.WHITE)
    }

    private fun resetAllFields() {
        fromAccount = null
        toAccount = null
        amount = "0"

        fromAccountTextView.text = "From"
        toAccountTextView.text = "To"
        noteEditText.setText("")
        dateTimeTextView.text = "Select Date & Time"
        updateAmountText()
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
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.set(
                            selectedYear, selectedMonth, selectedDay,
                            selectedHour, selectedMinute
                        )
                        selectedTimestamp = selectedCalendar.timeInMillis
                        updateDateTimeText(selectedTimestamp)
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

    private fun updateDateTimeText(timestamp: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = getMonthName(calendar.get(Calendar.MONTH))
        val year = calendar.get(Calendar.YEAR)
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"

        val formatted = String.format("%02d %s %04d, %02d:%02d %s", day, month, year, hour, minute, amPm)
        dateTimeTextView.text = formatted
    }

    private fun getMonthName(month: Int): String {
        val months = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        return months[month]
    }
}