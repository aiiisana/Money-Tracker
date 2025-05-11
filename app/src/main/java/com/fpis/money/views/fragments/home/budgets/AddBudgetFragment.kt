package com.fpis.money.views.fragments.home.budgets

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fpis.money.R
import com.fpis.money.databinding.FragmentAddBudgetBinding
import com.fpis.money.models.Budget
import com.fpis.money.models.Card
import com.fpis.money.models.Category
import com.fpis.money.utils.showCustomToast
import com.fpis.money.utils.ToastType
import com.fpis.money.views.fragments.add.AmountInputBottomSheet
import com.fpis.money.views.fragments.add.category.CategoryBottomSheet
import com.fpis.money.views.fragments.add.category.icon.IconPickerBottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddBudgetFragment : Fragment() {
    private var _binding: FragmentAddBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BudgetViewModel

    // ─── Selected state ────────────────────────────────
    private var selectedCategoryId   = 0
    private var selectedCategoryName = ""
    private var selectedIconRes      = R.drawable.ic_food
    private var selectedColorRes     = R.color.teal_200
    private var selectedColorHex     = "#66FFA3"
    private var selectedAmount = 0.0
    private lateinit var accountNameTextView: TextView
    private lateinit var accountSelectionLayout: ImageView
    private var selectedPaymentMethod: String = "Select account"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[BudgetViewModel::class.java]

        binding.etBudgetName.setText("")                                    // blank name
        binding.etBudgetName.hint = getString(R.string.hint_budget_name)    // e.g. "Enter budget name"
        binding.etBudgetAmount.setText("₸${String.format("%,.2f", 0.0)}")    // default zero
        binding.etBudgetAmount.setOnClickListener { showAmountInput() }

        accountNameTextView = view.findViewById(R.id.tv_accounts)
        accountSelectionLayout = view.findViewById(R.id.btn_accounts)

        accountSelectionLayout.setOnClickListener {
            showAccountSelectionDialog()
        }
        // Close button
        binding.btnClose.setOnClickListener { requireActivity().onBackPressed() }

        // CATEGORY picker
        binding.btnCategories.setOnClickListener {
            CategoryBottomSheet(isIncome = false) { cat: Category ->
                selectedCategoryId   = cat.id
                selectedCategoryName = cat.name
                binding.tvCategories.text = cat.name
            }.show(parentFragmentManager, "CategoryBottomSheet")
        }

        // ICON & COLOR picker
        binding.colorCircle.setOnClickListener {
            IconPickerBottomSheet(
                onSelectionComplete = { icon, color ->
                    selectedIconRes  = icon
                    selectedColorRes = color
                    selectedColorHex = String.format("#%06X",
                        0xFFFFFF and ContextCompat.getColor(requireContext(), color)
                    )
                    binding.colorCircle.setImageResource(icon)
                    binding.colorCircle.setColorFilter(
                        ContextCompat.getColor(requireContext(), color),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                },
                initialIconRes  = selectedIconRes,
                initialColorRes = selectedColorRes
            ).show(parentFragmentManager, "IconPicker")
        }

        // CREATE new budget
        binding.btnCreateBudget.setOnClickListener {
            if (selectedAmount <= 0.0) {
                showCustomToast(
                    requireContext(),
                    "Please enter an amount greater than zero",
                    ToastType.ERROR
                )
                return@setOnClickListener
            }
            val name = binding.etBudgetName.text.toString().ifBlank { "Untitled" }
            val amt  = binding.etBudgetAmount.text.toString()
                .replace("[^0-9.]".toRegex(), "")
                .toDoubleOrNull() ?: 0.0

            viewModel.addBudget(
                Budget(
                    category = if (selectedCategoryName.isBlank()) name else selectedCategoryName,
                    amount   = selectedAmount,
                    spent    = 0.0,
                    color    = selectedColorHex,
                    iconRes  = selectedIconRes,
                    colorRes = selectedColorRes
                )
            )
            requireActivity().onBackPressed()
        }
    }
    private fun showAmountInput() {
        val sheet = AmountInputBottomSheet { entered ->
            val v = entered.toDoubleOrNull() ?: 0.0
            selectedAmount = v
            binding.etBudgetAmount.setText("₸${String.format("%,.2f", v)}")
        }
        sheet.show(parentFragmentManager, "AmountInput")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    companion object {
        @JvmStatic fun newInstance() = AddBudgetFragment()
    }
}
