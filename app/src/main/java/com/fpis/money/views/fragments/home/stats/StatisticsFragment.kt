package com.fpis.money.views.fragments.home.stats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatisticsFragment : Fragment() {

    private lateinit var expenseChart: ExpenseChartView
    private lateinit var dateText: TextView
    private lateinit var categoryList: RecyclerView
    private lateinit var btnPrevDate: ImageView
    private lateinit var btnNextDate: ImageView
    private lateinit var btnBack: ImageView

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("kk", "KZ"))

    private lateinit var viewModel: StatisticsViewModel
    private val adapter = CategoryStatsAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expenseChart = view.findViewById(R.id.expense_chart)
        dateText = view.findViewById(R.id.date_text)
        categoryList = view.findViewById(R.id.category_list)
        btnPrevDate = view.findViewById(R.id.btn_prev_date)
        btnNextDate = view.findViewById(R.id.btn_next_date)
        btnBack = view.findViewById(R.id.btn_back)

        viewModel = ViewModelProvider(this).get(StatisticsViewModel::class.java)

        setupListeners()
        setupRecyclerView()
        updateDateText()
        observeViewModel()
    }

    private fun setupListeners() {
        btnPrevDate.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            updateDateText()
            loadExpenseData()
        }

        btnNextDate.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            updateDateText()
            loadExpenseData()
        }

        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }

    private fun setupRecyclerView() {
        categoryList.layoutManager = LinearLayoutManager(requireContext())
        categoryList.adapter = adapter
    }

    private fun updateDateText() {
        dateText.text = dateFormat.format(calendar.time).uppercase()
    }

    private fun loadExpenseData() {
        viewModel.getExpensesByDate(calendar.time).observe(viewLifecycleOwner) { categories ->
            adapter.updateItems(categories)

            // Update chart
            expenseChart.clearCategories()
            for (category in categories) {
                expenseChart.addCategory(category.name, category.amount.toFloat(), category.color)
            }
        }

        viewModel.getTotalExpenseByDate(calendar.time).observe(viewLifecycleOwner) { totalExpense ->
            val formattedAmount = "₸${String.format("%,.2f", totalExpense)}"
            expenseChart.setCenterText("Expense", formattedAmount)
        }
    }

    private fun observeViewModel() {
        loadExpenseData()
    }

    data class CategoryStats(
        val name: String,
        val amount: Double,
        val color: Int,
        val progress: Int = 0
    )

    inner class CategoryStatsAdapter(private var items: List<CategoryStats>) :
        RecyclerView.Adapter<CategoryStatsAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val categoryName: TextView = view.findViewById(R.id.category_name)
            val categoryAmount: TextView = view.findViewById(R.id.category_amount)
            val categoryProgress: ProgressBar = view.findViewById(R.id.category_progress)
        }

        fun updateItems(newItems: List<CategoryStats>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_caterogy_stats, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.categoryName.text = item.name
            holder.categoryAmount.text = currencyFormat.format(item.amount)
            holder.categoryProgress.progress = item.progress

            // Set progress bar color
            holder.categoryProgress.progressDrawable.setColorFilter(
                item.color, android.graphics.PorterDuff.Mode.SRC_IN
            )
        }

        override fun getItemCount() = items.size
    }

    companion object {
        @JvmStatic
        fun newInstance() = StatisticsFragment()
    }
}