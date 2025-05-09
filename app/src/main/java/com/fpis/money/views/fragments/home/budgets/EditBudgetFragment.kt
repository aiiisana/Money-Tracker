package com.fpis.money.views.fragments.home.budgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fpis.money.R
import com.fpis.money.databinding.FragmentEditBudgetBinding
import com.fpis.money.models.Budget
import com.fpis.money.models.Category
import com.fpis.money.utils.showCustomToast
import com.fpis.money.utils.ToastType
import com.fpis.money.views.fragments.add.AmountInputBottomSheet
import com.fpis.money.views.fragments.add.category.CategoryBottomSheet
import com.fpis.money.views.fragments.add.category.icon.IconPickerBottomSheet

class EditBudgetFragment : Fragment() {
    private var _binding: FragmentEditBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BudgetViewModel

    private var budgetId: Long = 0L
    private var budget: Budget? = null

    // ─── Selected state ────────────────────────────────
    private var selectedCategoryId   = 0
    private var selectedCategoryName = ""
    private var selectedIconRes      = R.drawable.ic_food
    private var selectedColorRes     = R.color.teal_200
    private var selectedColorHex     = "#66FFA3"
    private var selectedAmount       = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        budgetId = arguments?.getString(ARG_BUDGET_ID)?.toLongOrNull() ?: 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[BudgetViewModel::class.java]

        // 1) initialize amount field to 0 and wire up bottom sheet
        binding.etBudgetAmount.apply {
            setText("₸${String.format("%,.2f", 0.0)}")
            isFocusable = false
            isFocusableInTouchMode = false
            isCursorVisible = false
            isClickable = true
            setOnClickListener { showAmountInput() }
        }

        // 2) load existing budget from DB
        viewModel.getBudgetById(budgetId.toString())
            .observe(viewLifecycleOwner, Observer { b ->
                budget = b
                b?.let {
                    // populate fields & selected state
                    binding.etBudgetName.setText(it.category)
                    selectedCategoryName = it.category
                    binding.tvCategories.text = it.category

                    selectedAmount = it.amount
                    binding.etBudgetAmount.setText("₸${String.format("%,.2f", it.amount)}")

                    selectedIconRes  = it.iconRes ?: selectedIconRes
                    selectedColorRes = it.colorRes ?: selectedColorRes
                    selectedColorHex = it.color
                    // If you stored icon/color resources, pull them too; else leave defaults
                    binding.colorCircle.setImageResource(selectedIconRes)
                    binding.colorCircle.setColorFilter(
                        ContextCompat.getColor(requireContext(), selectedColorRes),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            })

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
                    binding.colorCircle.setImageResource(icon)
                    binding.colorCircle.setColorFilter(
                        ContextCompat.getColor(requireContext(), color),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    selectedColorHex = String.format(
                        "#%06X",
                        0xFFFFFF and ContextCompat.getColor(requireContext(), color)
                    )
                },
                initialIconRes  = selectedIconRes,
                initialColorRes = selectedColorRes
            ).show(parentFragmentManager, "IconPicker")
        }

        // SAVE updates
        binding.btnSaveBudget.setOnClickListener {
            if (selectedAmount <= 0.0) {
                showCustomToast(
                    requireContext(),
                    "Please enter an amount greater than zero",
                    ToastType.ERROR
                )
                return@setOnClickListener
            }
            budget?.let {
                val newName = binding.etBudgetName.text.toString().ifBlank { it.category }
                viewModel.updateBudget(
                    it.copy(
                        category = if (selectedCategoryName.isBlank()) newName else selectedCategoryName,
                        amount   = selectedAmount,
                        iconRes  = selectedIconRes,
                        colorRes = selectedColorRes,
                        color    = selectedColorHex
                    )
                )
                requireActivity().onBackPressed()
            }
        }
    }

    private fun showAmountInput() {
        AmountInputBottomSheet { entered ->
            val v = entered.toDoubleOrNull() ?: 0.0
            selectedAmount = v
            binding.etBudgetAmount.setText("₸${String.format("%,.2f", v)}")
        }.show(parentFragmentManager, "AmountInput")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_BUDGET_ID = "budget_id"
        @JvmStatic fun newInstance(id: String) =
            EditBudgetFragment().apply {
                arguments = Bundle().apply { putString(ARG_BUDGET_ID, id) }
            }
    }
}
