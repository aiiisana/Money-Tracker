package com.fpis.money.views.fragments.home

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.fpis.money.R
import com.fpis.money.databinding.FragmentHomeBinding
import com.fpis.money.views.fragments.cards.AddCardFragment
import com.fpis.money.views.fragments.home.ExpenseChartView
import com.fpis.money.views.fragments.home.budgets.BudgetFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private var currentDate = Calendar.getInstance()
    private lateinit var expenseChartView: ExpenseChartView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the expense chart view
        expenseChartView = view.findViewById(R.id.expense_chart_view)

        // Make sure the chart view is not null
        if (expenseChartView == null) {
            // Log error or handle the case where the view is not found
            println("ERROR: ExpenseChartView not found in layout")
        } else {
            println("ExpenseChartView initialized successfully")
        }

        setupDateNavigation()
        setupTotalBalance()
        setupBudgetSection()
        setupCategoriesSection()
        observeViewModel()
    }

    private fun setupDateNavigation() {
        // Set current date
        updateDateDisplay()

        // Previous day button
        binding.r2h4v8l7940z.setOnClickListener {
            currentDate.add(Calendar.DAY_OF_MONTH, -1)
            updateDateDisplay()
            viewModel.loadDataForDate(currentDate.time)
        }

        // Next day button
        binding.reuvnntrs0pn.setOnClickListener {
            currentDate.add(Calendar.DAY_OF_MONTH, 1)
            updateDateDisplay()
            viewModel.loadDataForDate(currentDate.time)
        }

        // Date selector
        binding.r56haae8qe5.setOnClickListener {
            showDatePicker()
        }
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
        binding.rdfqt6bdy4v6.text = dateFormat.format(currentDate.time).uppercase()
    }

    private fun showDatePicker() {
        val datePickerDialog = android.app.DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            { _, year, month, dayOfMonth ->
                currentDate.set(Calendar.YEAR, year)
                currentDate.set(Calendar.MONTH, month)
                currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateDisplay()
                viewModel.loadDataForDate(currentDate.time)
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun setupTotalBalance() {
        // Set total balance
        binding.r40wus18jo1q.text = "₸760,000.00"

        // Toggle balance visibility
        binding.r2tggiv586wg.setOnClickListener {
            toggleBalanceVisibility()
        }
    }

    private fun toggleBalanceVisibility() {
        if (binding.r40wus18jo1q.text.toString().contains("*")) {
            binding.r40wus18jo1q.text = "₸760,000.00"
        } else {
            binding.r40wus18jo1q.text = "₸*********"
        }
    }

    private fun setupBudgetSection() {
        // Set budget values
        binding.r2gswwyrlq43.text = "₸483,000.00 left"
        binding.rg1ohu7vm6na.text = "-₸277,000 spent this month"

        // Set progress bar width based on budget spent percentage
        val totalBudget = 760000.00
        val spentBudget = 277000.00
        val spentPercentage = (spentBudget / totalBudget) * 100

        // We'll update the progress bar width in onResume when the view is laid out
        binding.rxveltjdzj6.post {
            val layoutParams = binding.rmrg11ps7xhr.layoutParams
            layoutParams.width = (binding.rxveltjdzj6.width * spentPercentage / 100).toInt()
            binding.rmrg11ps7xhr.layoutParams = layoutParams
        }

        // Set up category cards
        binding.rucdlzmkrjlp.text = "Entertainment"
        binding.rpenb33b77xg.text = "₸156,500 spent"

        binding.rb1jkxtvtiq7.text = "Food"
        binding.rn6ubd8927c.text = "₸80,000 spent"

        // Set up "All Budgets" button
        binding.rf3vvw5b4tjd.setOnClickListener {
            showBudgetFragment()
            // Navigate to all budgets screen
        }
        binding.r7qkhfwpji9m.setOnClickListener {
            showBudgetFragment()
        }
    }

    private fun setupCategoriesSection() {
        // Set up the expense chart
        setupExpenseChart()

        // Set up "Statistics" button
        binding.ryndljmqcc9.setOnClickListener {
            // Navigate to statistics screen
        }
    }

    private fun setupExpenseChart() {
        // Set the text
        binding.rrrdy9mslim.text = "Expense"
        binding.rk44edj4rkt.text = "₸270,000"

        // Set up the chart data with the correct colors matching the UI
        val categories = listOf(
            ExpenseChartView.ExpenseCategory("Entertainment", 156500f, "#66FFA3"),  // Green
            ExpenseChartView.ExpenseCategory("Food", 80000f, "#FF9366"),            // Orange
            ExpenseChartView.ExpenseCategory("Transportation", 25000f, "#3DB9FF"),  // Blue
            ExpenseChartView.ExpenseCategory("Shopping", 15500f, "#FFD966"),        // Yellow
            ExpenseChartView.ExpenseCategory("Other", 10000f, "#FF66D4")            // Pink
        )

        // Update the chart with data
        if (::expenseChartView.isInitialized) {
            try {
                expenseChartView.setData(categories)
                println("Chart data set successfully")
            } catch (e: Exception) {
                println("Error setting chart data: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("ExpenseChartView not initialized yet")
        }
    }

    private fun observeViewModel() {
        // Observe data changes from ViewModel
        viewModel.totalBalance.observe(viewLifecycleOwner) { balance ->
            binding.r40wus18jo1q.text = "₸$balance"
        }

        viewModel.budgetData.observe(viewLifecycleOwner) { budget ->
            binding.r2gswwyrlq43.text = "₸${budget.remaining} left"
            binding.rg1ohu7vm6na.text = "-₸${budget.spent} spent this month"

            // Update progress bar
            val spentPercentage = budget.spent.replace(",", "").toDouble() /
                    budget.total.replace(",", "").toDouble() * 100
            binding.rxveltjdzj6.post {
                val layoutParams = binding.rmrg11ps7xhr.layoutParams
                layoutParams.width = (binding.rxveltjdzj6.width * spentPercentage / 100).toInt()
                binding.rmrg11ps7xhr.layoutParams = layoutParams
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            if (categories.isNotEmpty()) {
                // Update entertainment category
                val entertainment = categories.find { it.name == "Entertainment" }
                entertainment?.let {
                    binding.rucdlzmkrjlp.text = it.name
                    binding.rpenb33b77xg.text = "₸${it.spent} spent"
                    binding.rpenb33b77xg.setTextColor(Color.parseColor(it.color))
                }

                // Update food category
                val food = categories.find { it.name == "Food" }
                food?.let {
                    binding.rb1jkxtvtiq7.text = it.name
                    binding.rn6ubd8927c.text = "₸${it.spent} spent"
                    binding.rn6ubd8927c.setTextColor(Color.parseColor(it.color))
                }

                // Update total expense
                val totalExpense = categories.sumOf {
                    it.spent.replace(",", "").toDouble()
                }
                binding.rk44edj4rkt.text = "₸${totalExpense.toInt()}"

                // Update chart data
                if (::expenseChartView.isInitialized) {
                    try {
                        val chartCategories = categories.map {
                            ExpenseChartView.ExpenseCategory(
                                it.name,
                                it.spent.replace(",", "").toFloat(),
                                it.color
                            )
                        }
                        expenseChartView.setData(chartCategories)
                    } catch (e: Exception) {
                        println("Error updating chart data: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun animateBalanceChange(newBalance: String) {
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 300
        }
        binding.r40wus18jo1q.startAnimation(fadeIn)
        binding.r40wus18jo1q.text = "₸$newBalance"
    }

    private fun showBudgetFragment() {
        // Create a new instance of AddCardFragment
        val budgetFragment = BudgetFragment.newInstance()

        // Get the fragment manager from the activity
        val fragmentManager = parentFragmentManager

        // Add the AddCardFragment to the container and hide the current fragment
        fragmentManager.beginTransaction()
            .add(R.id.fragment_container, budgetFragment)
            .hide(this)
            .addToBackStack("budget")
            .commit()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}