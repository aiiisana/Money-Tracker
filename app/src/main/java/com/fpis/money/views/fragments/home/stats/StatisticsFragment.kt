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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.fpis.money.databinding.FragmentStatisticsBinding
import com.fpis.money.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatisticsFragment : Fragment() {

    private lateinit var expenseChart: ExpenseChartView
    private lateinit var expenseAmount: TextView
    private lateinit var dateText: TextView
    private lateinit var categoryList: RecyclerView
    private lateinit var btnPrevDate: ImageView
    private lateinit var btnNextDate: ImageView
    private lateinit var btnBack: ImageView

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("kk", "KZ"))

    private val categories = listOf(
        CategoryStats("Food", 3430.0, Color.parseColor("#FFDA66")),
        CategoryStats("Entertainment", 1500.0, Color.parseColor("#FF9466")),
        CategoryStats("Transport", 210.0, Color.parseColor("#66FFA3")),
        CategoryStats("Health", 560.0, Color.parseColor("#FF66A3")),
        CategoryStats("Shopping", 850.0, Color.parseColor("#66D9FF"))
    )

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

        setupListeners()
        setupRecyclerView()
        updateDateText()
        loadExpenseData()
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
        categoryList.adapter = CategoryStatsAdapter(categories)
    }

    private fun updateDateText() {
        dateText.text = dateFormat.format(calendar.time).uppercase()
    }

    private fun loadExpenseData() {
        // In a real app, you would load data from your database based on the selected date
        // For now, we'll use our sample data

        val totalExpense = categories.sumOf { it.amount }
        val formattedAmount = "₸${String.format("%,.2f", totalExpense)}"

        // Set the center text in the chart
        expenseChart.clearCategories()
        expenseChart.setCenterText("Expense", formattedAmount)

        // Add categories to the chart
        for (category in categories) {
            expenseChart.addCategory(category.name, category.amount.toFloat(), category.color)
        }
    }

    data class CategoryStats(
        val name: String,
        val amount: Double,
        val color: Int,
        val progress: Int = ((amount / 5350.43) * 100).toInt() // Calculate percentage of total
    )

    inner class CategoryStatsAdapter(private val items: List<CategoryStats>) :
        RecyclerView.Adapter<CategoryStatsAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val categoryName: TextView = view.findViewById(R.id.category_name)
            val categoryAmount: TextView = view.findViewById(R.id.category_amount)
            val categoryProgress: ProgressBar = view.findViewById(R.id.category_progress)
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