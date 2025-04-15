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
import androidx.lifecycle.ViewModelProvider
import com.fpis.money.R
import com.fpis.money.databinding.FragmentBudgetListBinding
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast
import com.fpis.money.models.Budget

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BudgetViewModel
    private lateinit var adapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)


        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = BudgetAdapter { budget, view ->
            showBudgetOptionsMenu(budget, view)
        }
        binding.budgetRecyclerView.adapter = adapter
        binding.budgetRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnAddBudget.setOnClickListener {
            val addBudgetFragment = AddBudgetFragment.newInstance()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, addBudgetFragment)
                .addToBackStack("addBudget")
                .commit()
        }
    }

    private fun observeViewModel() {
        viewModel.budgets.observe(viewLifecycleOwner) { budgets ->
            adapter.submitList(budgets)
        }
    }

    private fun showBudgetOptionsMenu(budget: Budget, anchorView: View) {
        BudgetOption(
            requireContext(),
            onEditClick = {
                val editBudgetFragment = EditBudgetFragment.newInstance(budget.id.toString())
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, editBudgetFragment)
                    .addToBackStack("editBudget")
                    .commit()
            },
            onDeleteClick = {
                showDeleteConfirmationDialog(budget)
            }
        ).show()
    }

    private fun showDeleteConfirmationDialog(budget: Budget) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.budget_delete_conf)

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            attributes.windowAnimations = R.style.DialogAnimation
        }

        val titleTextView = dialog.findViewById<TextView>(R.id.tv_dialog_title)
        titleTextView.text = "Delete Budget"

        val messageTextView = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        messageTextView.text = "Are you sure you want to delete the ${budget.category} budget?"

        val cancelButton = dialog.findViewById<Button>(R.id.btn_cancel)
        val deleteButton = dialog.findViewById<Button>(R.id.btn_delete)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        deleteButton.setOnClickListener {
            viewModel.deleteBudget(budget.id.toString())
            showCustomToast(requireContext(), "${budget.category} budget deleted", ToastType.SUCCESS)
            dialog.dismiss()
        }

        dialog.show()
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