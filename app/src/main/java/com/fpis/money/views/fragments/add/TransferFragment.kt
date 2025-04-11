package com.fpis.money.views.fragments.add

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fpis.money.R
import com.fpis.money.models.Card
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
            val timestamp = System.currentTimeMillis()

            if (parsedAmount == null || parsedAmount <= 0f) {
                Toast.makeText(requireContext(), "Enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fromAccount.isNullOrBlank() || toAccount.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Select both accounts", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fromAccount == toAccount) {
                Toast.makeText(requireContext(), "Cannot transfer to the same account", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addViewModel.saveTransfer(
                fromAccount = fromAccount!!,
                toAccount = toAccount!!,
                amount = parsedAmount,
                date = timestamp,
                notes = note
            )

            Toast.makeText(requireContext(), "Transfer saved", Toast.LENGTH_SHORT).show()
            resetAllFields()
            parentFragmentManager.popBackStack()
        }
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
                Toast.makeText(requireContext(), "Failed to load accounts", Toast.LENGTH_SHORT).show()
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
        updateAmountText()
    }
}