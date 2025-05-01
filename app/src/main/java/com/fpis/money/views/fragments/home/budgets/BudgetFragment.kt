// BudgetFragment.kt
package com.fpis.money.views.fragments.home.budgets

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fpis.money.R
import com.fpis.money.databinding.FragmentBudgetBinding
import com.fpis.money.models.Budget
import com.fpis.money.utils.ToastType
import com.fpis.money.utils.showCustomToast

class BudgetFragment : Fragment() {

    // 1) Use the wrapper binding
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BudgetViewModel by viewModels()
    private lateinit var adapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 2) Inflate fragment_budget.xml (which includes fragment_budget_list.xml)
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 3) Grab the inner list binding
        val listBinding = binding.budgetList

        // RecyclerView
        adapter = BudgetAdapter { budget, _ -> showBudgetOptionsMenu(budget) }
        listBinding.budgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        listBinding.budgetRecyclerView.adapter = adapter

        // Observe data
        viewModel.budgets.observe(viewLifecycleOwner) { adapter.submitList(it) }

        // Back button in header
        listBinding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Add-budget button in header
        listBinding.btnAddBudget.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddBudgetFragment.newInstance())
                .addToBackStack("addBudget")
                .commit()
        }
    }

    private fun showBudgetOptionsMenu(budget: Budget) {
        BudgetOption(
            requireContext(),
            onEditClick = { editBudget(budget) },
            onDeleteClick = { deleteBudget(budget) }
        ).show()
    }

    private fun editBudget(budget: Budget) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                EditBudgetFragment.newInstance(budget.id.toString())
            )
            .addToBackStack("editBudget")
            .commit()
    }

    private fun deleteBudget(budget: Budget) {
        viewModel.deleteBudget(budget.id.toString())
        showCustomToast(
            requireContext(),
            "${budget.category} budget deleted",
            ToastType.SUCCESS
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic fun newInstance() = BudgetFragment()
    }
}
