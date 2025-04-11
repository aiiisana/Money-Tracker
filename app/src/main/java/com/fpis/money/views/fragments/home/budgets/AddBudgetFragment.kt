package com.fpis.money.views.fragments.home.budgets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.fpis.money.R
import com.fpis.money.views.fragments.cards.ToastType
import com.fpis.money.views.fragments.cards.showCustomToast

class AddBudgetFragment : Fragment() {

    private val viewModel: BudgetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up close button
        view.findViewById<ImageView>(R.id.btn_close).setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Set up create button
        view.findViewById<Button>(R.id.btn_create_budget).setOnClickListener {
            createBudget()
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

    private fun createBudget() {
        val budgetName = view?.findViewById<EditText>(R.id.et_budget_name)?.text.toString()
        val budgetAmount = view?.findViewById<EditText>(R.id.et_budget_amount)?.text.toString()
            .replace("[^0-9.]".toRegex(), "")
            .toDoubleOrNull() ?: 0.0

        // Create a new budget
        val newBudget = Budget(
            id = System.currentTimeMillis().toString(),
            category = budgetName,
            amount = budgetAmount,
            spent = 0.0,
            color = "#66FFA3", // Default green color
            percentage = 0
        )

        // Add the budget to the view model
        viewModel.addBudget(newBudget)

        // Show success toast
        showCustomToast(requireContext(), "Budget created successfully", ToastType.SUCCESS)

        // Go back to budget list
        requireActivity().onBackPressed()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddBudgetFragment()
    }
}