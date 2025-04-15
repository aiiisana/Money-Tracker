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
import androidx.lifecycle.ViewModelProvider
import com.fpis.money.R
import com.fpis.money.views.fragments.home.stats.ExpenseChartView
import com.fpis.money.databinding.FragmentHomeBinding
import com.fpis.money.views.fragments.home.budgets.BudgetFragment
import com.fpis.money.views.fragments.home.stats.StatisticsFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
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

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Initialize the expense chart view
        expenseChartView = view.findViewById(R.id.expense_chart_view)

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
            showStatisticsFragment()
        }
        binding.rxafjah9m8vq.setOnClickListener {
            showStatisticsFragment()
        }
    }

    private fun setupExpenseChart() {
        // Set up the chart data with the correct colors matching the UI
        val categories = listOf(
            ExpenseChartView.ExpenseCategoryInput("Entertainment", 156500f, "#66FFA3"),  // Green
            ExpenseChartView.ExpenseCategoryInput("Food", 80000f, "#FF9366"),            // Orange
            ExpenseChartView.ExpenseCategoryInput("Transportation", 25000f, "#3DB9FF"),  // Blue
            ExpenseChartView.ExpenseCategoryInput("Shopping", 15500f, "#FFD966"),        // Yellow
            ExpenseChartView.ExpenseCategoryInput("Other", 10000f, "#FF66D4")            // Pink
        )

        // Update the chart with data
        if (::expenseChartView.isInitialized) {
            try {
                expenseChartView.setData(categories)
                expenseChartView.setCenterText("Expense", "₸270,000")
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
            binding.rxveltjdzj6.post {
                val layoutParams = binding.rmrg11ps7xhr.layoutParams
                layoutParams.width = (binding.rxveltjdzj6.width * budget.percentage / 100).toInt()
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
                val food = categories.find { it.name == "Food & Drink" }
                food?.let {
                    binding.rb1jkxtvtiq7.text = it.name
                    binding.rn6ubd8927c.text = "₸${String.format("%,.2f", it.spent)} spent"
                    binding.rn6ubd8927c.setTextColor(android.graphics.Color.parseColor(it.color))
                }

                // Update chart data
                if (::expenseChartView.isInitialized) {
                    try {
                        val chartCategories = categories.map {
                            ExpenseChartView.ExpenseCategoryInput(
                                it.name,
                                it.spent.toFloat(),
                                it.color
                            )
                        }

                        // Calculate total expense
                        val totalExpense = categories.sumOf {
                            it.spent
                        }
                        val totalExpenseFormatted = "₸${String.format("%,.2f", totalExpense)}"

                        expenseChartView.setData(chartCategories)
                        expenseChartView.setCenterText("Expense", totalExpenseFormatted)
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
    private fun showStatisticsFragment() {
        val statisticsFragment = StatisticsFragment.newInstance()

        val fragmentManager = parentFragmentManager

        fragmentManager.beginTransaction()
            .add(R.id.fragment_container, statisticsFragment)
            .hide(this)
            .addToBackStack("statistics")
            .commit()
    }
    companion object {
        fun newInstance() = HomeFragment()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}