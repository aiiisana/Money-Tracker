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

        // Load existing budget
        viewModel.getBudgetById(budgetId.toString())
            .observe(viewLifecycleOwner, Observer { b ->
                budget = b
                b?.let {
                    selectedCategoryName = it.category
                    selectedColorHex     = it.color
                    // if you also stored iconRes/colorRes in your DB, pull them here
                    binding.etBudgetName.setText(it.category)
                    binding.etBudgetAmount.setText(String.format("%,.2f", it.amount))
                    binding.tvCategories.text = it.category
                    // make sure colorCircle is ImageView
                    binding.colorCircle.setImageResource(selectedIconRes)
                    binding.colorCircle.setColorFilter(
                        ContextCompat.getColor(requireContext(), selectedColorRes)
                    )
                }
            })

        // Close
        binding.btnClose.setOnClickListener { requireActivity().onBackPressed() }

        // CATEGORY re‐picker
        binding.btnCategories.setOnClickListener {
            CategoryBottomSheet(isIncome = false) { cat: Category ->
                selectedCategoryId   = cat.id
                selectedCategoryName = cat.name
                binding.tvCategories.text = cat.name
            }.show(parentFragmentManager, "CategoryBottomSheet")
        }

        // ICON & COLOR re‐picker
        binding.colorCircle.setOnClickListener {
            IconPickerBottomSheet(
                onSelectionComplete = { icon, color ->
                    selectedIconRes  = icon
                    selectedColorRes = color
                    binding.colorCircle.setImageResource(icon)
                    binding.colorCircle.setColorFilter(
                        ContextCompat.getColor(requireContext(), color)
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
            budget?.let {
                val newName = binding.etBudgetName.text.toString().ifBlank { it.category }
                val newAmt  = binding.etBudgetAmount.text.toString()
                    .replace("[^0-9.]".toRegex(), "")
                    .toDoubleOrNull() ?: it.amount

                viewModel.updateBudget(
                    it.copy(
                        category = if (selectedCategoryName.isBlank()) newName else selectedCategoryName,
                        amount   = newAmt,
                        color    = selectedColorHex
                    )
                )
                requireActivity().onBackPressed()
            }
        }
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
