package com.fpis.money.views.fragments.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fpis.money.R
import com.fpis.money.views.fragments.home.HorizontalSpaceItemDecoration
import com.fpis.money.databinding.FragmentHomeBinding
import com.fpis.money.views.fragments.home.budgets.BudgetFragment
import com.fpis.money.views.fragments.home.stats.ExpenseChartView
import com.fpis.money.views.fragments.home.stats.StatisticsFragment
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var chartView: ExpenseChartView
    private val statsAdapter = HomeCategoryAdapter()
    private val budgetAdapter = BudgetCategoryAdapter()

    private val calendar = Calendar.getInstance()
    private var lastKnownBalance = "–"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        chartView = binding.expenseChartView

        setupBudgetCategoriesRecycler()
        setupStatsRecycler()
        setupDateNavigation()
        setupTotalBalanceToggle()
        setupNavigationButtons()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBudgetCategoriesRecycler() = with(binding.budgetCategoriesRecyclerView) {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = budgetAdapter
            addItemDecoration(
                HorizontalSpaceItemDecoration(resources
                    .getDimensionPixelSize(R.dimen.category_chip_spacing))
            )
    }

    private fun setupStatsRecycler() = with(binding.statsRecyclerView) {
        isNestedScrollingEnabled = false    // allow wrap_content inside ScrollView
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = statsAdapter
        addItemDecoration(
            HorizontalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.category_chip_spacing))
        )
    }

    private fun observeViewModel() = with(viewModel) {
        totalExpense.observe(viewLifecycleOwner) { centerText ->
            chartView.setCenterText("Expense", centerText)
        }

        categoryStats.observe(viewLifecycleOwner) { list ->
            chartView.setData(list.map {
                ExpenseChartView.ExpenseCategoryInput(
                    it.name,
                    it.amount.toFloat(),
                    String.format("#%06X", 0xFFFFFF and it.color)
                )
            })
            statsAdapter.submit(list)
        }

        budgetSummary.observe(viewLifecycleOwner) { sum ->
            lastKnownBalance = "₸%,.2f".format(sum.total)
            binding.r40wus18jo1q.text = lastKnownBalance

            binding.r2gswwyrlq43.text = "₸%,.2f left".format(sum.remaining)
            binding.rg1ohu7vm6na.text = "-₸%,.2f spent this month".format(sum.spent)

            binding.rxveltjdzj6.post {
                val lp = binding.rmrg11ps7xhr.layoutParams
                lp.width = (binding.rxveltjdzj6.width * sum.percentage / 100.0).toInt()
                binding.rmrg11ps7xhr.layoutParams = lp
            }
        }
        viewModel.budgets.observe(viewLifecycleOwner) { list ->
            budgetAdapter.submit(list)
        }
    }

    private fun setupDateNavigation() {
        updateDateDisplay()
        binding.r2h4v8l7940z.setOnClickListener { shiftDate(-1) }
        binding.reuvnntrs0pn.setOnClickListener { shiftDate(+1) }
        binding.r56haae8qe5.setOnClickListener { showDatePicker() }
    }

    private fun shiftDate(delta: Int) {
        calendar.add(Calendar.DAY_OF_MONTH, delta)
        updateDateDisplay()
        viewModel.setDate(calendar.time)
    }

    private fun updateDateDisplay() {
        binding.rdfqt6bdy4v6.text =
            SimpleDateFormat("dd MMM yyyy", Locale.US).format(calendar.time).uppercase()
    }

    private fun showDatePicker() = android.app.DatePickerDialog(
        requireContext(), R.style.DatePickerTheme,
        { _, y, m, d ->
            calendar.set(y, m, d)
            updateDateDisplay()
            viewModel.setDate(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()

    private fun setupTotalBalanceToggle() {
        binding.r2tggiv586wg.setOnClickListener {
            binding.r40wus18jo1q.text =
                if (binding.r40wus18jo1q.text.contains("*")) lastKnownBalance else "₸*********"
        }
    }

    private fun setupNavigationButtons() {
        binding.rf3vvw5b4tjd.setOnClickListener { showBudget() }
        binding.r7qkhfwpji9m.setOnClickListener { showBudget() }
        binding.ryndljmqcc9.setOnClickListener { showStatistics() }
        binding.statisticsSection.setOnClickListener { showStatistics() }
    }

    private fun showBudget() {
        parentFragmentManager.beginTransaction()
            .add(R.id.fragment_container, BudgetFragment.newInstance())
            .hide(this)
            .addToBackStack(null)
//            .addToBackStack("budget")
            .commit()
    }

    private fun showStatistics() =
        parentFragmentManager.beginTransaction()
            .add(R.id.fragment_container, StatisticsFragment.newInstance())
            .hide(this)
            .addToBackStack(null)
//            .addToBackStack("statistics")
            .commit()

    companion object {
        fun newInstance() = HomeFragment()
    }
}
