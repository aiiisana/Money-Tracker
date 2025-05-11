package com.fpis.money.views.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fpis.money.databinding.FragmentAdminStatsBinding
import com.fpis.money.utils.showCustomToast
import com.fpis.money.utils.ToastType
import com.fpis.money.viewmodels.admin.AdminStatsViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.flow.collect

class AdminStatsFragment : Fragment() {
    private var _binding: FragmentAdminStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminStatsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCharts()
        observeViewModel()
        setupRefreshListener()
    }

    private fun setupRefreshListener() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadStats()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.loading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.swipeRefresh.isRefreshing = isLoading
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.stats.collect { stats ->
                stats?.let {
                    updateStatsUI(it)
                    updateCharts(it)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collect { error ->
                error?.let {
                    showCustomToast(requireContext(), it, ToastType.ERROR)
                }
            }
        }
    }

    private fun updateStatsUI(stats: AdminStatsViewModel.StatsData) {
        with(binding) {
            tvTotalUsers.text = stats.totalUsers.toString()
            tvActiveUsers.text = stats.activeUsers.toString()
            tvTotalTransactions.text = stats.totalTransactions.toString()
            tvAvgTransactions.text = String.format("%.1f", stats.avgTransactionsPerUser)
            tvIncomeAmount.text = String.format("₸%,.2f", stats.totalIncome)
            tvExpenseAmount.text = String.format("₸%,.2f", stats.totalExpenses)
        }
    }

    private fun setupCharts() {
        setupChart(binding.chartTransactions)
        setupChart(binding.chartFinancial)
    }

    private fun setupChart(chart: BarChart) {
        with(chart) {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelRotationAngle = -45f
            }

            axisLeft.setDrawGridLines(true)
            axisRight.isEnabled = false

            legend.isEnabled = true
            animateY(1000)
        }
    }

    private fun updateCharts(stats: AdminStatsViewModel.StatsData) {
        val transactionEntries = listOf(
            BarEntry(0f, stats.transactionsByType["expense"]?.toFloat() ?: 0f),
            BarEntry(1f, stats.transactionsByType["income"]?.toFloat() ?: 0f)
        )

        val transactionDataSet = BarDataSet(transactionEntries, "Transactions by Type").apply {
            colors = listOf(
                resources.getColor(com.fpis.money.R.color.red),
                resources.getColor(com.fpis.money.R.color.green)
            )
            valueTextSize = 12f
        }

        binding.chartTransactions.data = BarData(transactionDataSet).apply {
            barWidth = 0.4f
            setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            })
        }
        binding.chartTransactions.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Expenses", "Income"))
        binding.chartTransactions.invalidate()

        // Transactions by day chart
        val days = stats.transactionsByDay.keys.sorted()
        val dayEntries = days.mapIndexed { index, day ->
            BarEntry(index.toFloat(), stats.transactionsByDay[day]?.toFloat() ?: 0f)
        }

        val dayDataSet = BarDataSet(dayEntries, "Transactions by Day").apply {
            color = resources.getColor(com.fpis.money.R.color.green)
            valueTextSize = 10f
        }

        binding.chartFinancial.data = BarData(dayDataSet).apply {
            setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            })
        }
        binding.chartFinancial.xAxis.valueFormatter = IndexAxisValueFormatter(days)
        binding.chartFinancial.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}