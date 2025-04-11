package com.fpis.money.views.fragments.home.budgets

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fpis.money.R
import com.fpis.money.databinding.FragmentBudgetListBinding
import com.fpis.money.views.fragments.home.budgets.BudgetAdapter
import com.fpis.money.views.fragments.home.budgets.BudgetViewModel
import com.fpis.money.views.fragments.home.budgets.Budget
import com.fpis.money.databinding.FragmentHomeBinding
import com.fpis.money.views.fragments.cards.showCustomToast
import com.fpis.money.views.fragments.cards.ToastType
import com.fpis.money.views.fragments.cards.placeholder.PlaceholderContent

class BudgetFragment : Fragment() {

    // private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private var _binding: FragmentBudgetListBinding? = null
    private val viewModel: BudgetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    // In BudgetFragment.kt
    private fun setupRecyclerView() {
        val adapter = BudgetAdapter { budget, view ->
            // Show popup menu when clicking on the 3-dot menu
            showBudgetOptionsMenu(budget, view)
        }
        binding.budgetRecyclerView.adapter = adapter

        // Add this line to set the layout manager
        binding.budgetRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        // Load sample data for now
        adapter.submitList(getSampleBudgets())  // Remove the .take(5) to show all budgets
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnAddBudget.setOnClickListener {
            // Navigate to add budget screen
            // findNavController().navigate(R.id.action_budgetFragment_to_addBudgetFragment)
            val addBudgetFragment = AddBudgetFragment.newInstance()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, addBudgetFragment)
                .addToBackStack("addBudget")
                .commit()
           // showCustomToast(requireContext(), "New budget added", ToastType.SUCCESS)
        }

    }

    private fun observeViewModel() {
        viewModel.budgets.observe(viewLifecycleOwner) { budgets ->
            (binding.budgetRecyclerView.adapter as BudgetAdapter).submitList(budgets.take(5))
        }
    }

    // Make sure this is working in BudgetFragment.kt
    private fun showBudgetOptionsMenu(budget: Budget, anchorView: View) {
        BudgetOption(
            requireContext(),
            onEditClick = {
                val editBudgetFragment = EditBudgetFragment.newInstance(budget.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, editBudgetFragment)
                    .addToBackStack("editBudget")
                    .commit()
                // showCustomToast(requireContext(), "${budget.category} budget updated", ToastType.SUCCESS)
            },
            onDeleteClick = {
                showDeleteConfirmationDialog(budget)
            }
        ).show()
    }


    private fun showDeleteConfirmationDialog(budget: Budget) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.budget_delete_conf)

        // Set up dialog window properties
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            attributes.windowAnimations = R.style.DialogAnimation
        }

        // Update dialog title and message
        val titleTextView = dialog.findViewById<TextView>(R.id.tv_dialog_title)
        titleTextView.text = "Delete Budget"

        val messageTextView = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        messageTextView.text = "Are you sure you want to delete the ${budget.category} budget?"

        // Set up click listeners
        val cancelButton = dialog.findViewById<Button>(R.id.btn_cancel)
        val deleteButton = dialog.findViewById<Button>(R.id.btn_delete)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        deleteButton.setOnClickListener {
            // Delete the budget
            viewModel.deleteBudget(budget.id)
            showCustomToast(requireContext(), "${budget.category} budget deleted", ToastType.SUCCESS)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getSampleBudgets(): List<Budget> {
        return listOf(
            Budget(
                id = "1",
                category = "Entertainment",
                amount = 100000.0,
                spent = 54503.0,
                color = "#66FFA3",
                percentage = 19
            ),
            Budget(
                id = "2",
                category = "Food",
                amount = 50000.0,
                spent = 54503.0,
                color = "#FF9366",
                percentage = 123
            ),
            Budget(
                id = "3",
                category = "Transport",
                amount = 10000.0,
                spent = 0.0,
                color = "#3DB9FF",
                percentage = 0
            )
        )
    }
    companion object {
        @JvmStatic
        fun newInstance() = BudgetFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}