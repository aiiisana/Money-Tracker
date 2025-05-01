package com.fpis.money.views.fragments.home.budgets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fpis.money.R
import com.fpis.money.databinding.FragmentAddBudgetBinding
import com.fpis.money.models.Budget
import com.fpis.money.models.Category
import com.fpis.money.views.fragments.add.category.CategoryBottomSheet
import com.fpis.money.views.fragments.add.category.icon.IconPickerBottomSheet

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[BudgetViewModel::class.java]

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
                    // now binding.colorCircle is an ImageView
                    binding.colorCircle.setImageResource(icon)
                    binding.colorCircle.setColorFilter(
                        ContextCompat.getColor(requireContext(), color)
                    )
                    // also update hex for saving
                    selectedColorHex = String.format(
                        "#%06X",
                        0xFFFFFF and ContextCompat.getColor(requireContext(), color)
                    )
                },
                initialIconRes  = selectedIconRes,
                initialColorRes = selectedColorRes
            ).show(parentFragmentManager, "IconPicker")
        }

        // CREATE new budget
        binding.btnCreateBudget.setOnClickListener {
            val name = binding.etBudgetName.text.toString().ifBlank { "Untitled" }
            val amt  = binding.etBudgetAmount.text.toString()
                .replace("[^0-9.]".toRegex(), "")
                .toDoubleOrNull() ?: 0.0

            viewModel.addBudget(
                Budget(
                    category = if (selectedCategoryName.isBlank()) name else selectedCategoryName,
                    amount   = amt,
                    spent    = 0.0,
                    color    = selectedColorHex
                )
            )
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic fun newInstance() = AddBudgetFragment()
    }
}
