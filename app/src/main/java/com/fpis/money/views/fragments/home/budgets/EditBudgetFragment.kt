package com.fpis.money.views.fragments.home.budgets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.viewModels
import com.fpis.money.R
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast

class EditBudgetFragment : Fragment() {

    private val viewModel: BudgetViewModel by viewModels()
    private var budgetId: String? = null
    private var budget: Budget? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            budgetId = it.getString(ARG_BUDGET_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load budget data
        loadBudgetData()

        // Set up close button
        view.findViewById<ImageView>(R.id.btn_close).setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Set up save button
        view.findViewById<Button>(R.id.btn_save_budget).setOnClickListener {
            saveBudget()
        }

        // Set up color picker
        view.findViewById<View>(R.id.color_circle).setOnClickListener {
            // TODO: Show color picker dialog
        }

        // Set up accounts selector
        view.findViewById<ImageView>(R.id.btn_accounts).setOnClickListener {
            // TODO: Show accounts selection dialog
        }

        // Set up categories selector
        view.findViewById<ImageView>(R.id.btn_categories).setOnClickListener {
            // TODO: Show categories selection dialog
        }
    }

    private fun loadBudgetData() {
        budgetId?.let { id ->
            // Get budget from view model
            budget = viewModel.getBudgetById(id)

            // Populate UI with budget data
            budget?.let { budget ->
                view?.findViewById<EditText>(R.id.et_budget_name)?.setText(budget.category)
                view?.findViewById<EditText>(R.id.et_budget_amount)?.setText("₸${String.format("%,.2f", budget.amount)}")

                // TODO: Set color circle based on budget color
            }
        }
    }

    private fun saveBudget() {
        val budgetName = view?.findViewById<EditText>(R.id.et_budget_name)?.text.toString()
        val budgetAmount = view?.findViewById<EditText>(R.id.et_budget_amount)?.text.toString()
            .replace("[^0-9.]".toRegex(), "")
            .toDoubleOrNull() ?: 0.0

        // Update the budget
        budget?.let {
            val updatedBudget = it.copy(
                category = budgetName,
                amount = budgetAmount
                // Keep other properties the same
            )

            // Update the budget in the view model
            viewModel.updateBudget(updatedBudget)

            // Show success toast
            showCustomToast(requireContext(), "Budget updated successfully", ToastType.SUCCESS)

            // Go back to budget list
            requireActivity().onBackPressed()
        }
    }

    companion object {
        private const val ARG_BUDGET_ID = "budget_id"

        @JvmStatic
        fun newInstance(budgetId: String) = EditBudgetFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_BUDGET_ID, budgetId)
            }
        }
    }
}