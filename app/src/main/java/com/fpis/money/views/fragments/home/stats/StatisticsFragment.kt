package com.fpis.money.views.fragments.home.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fpis.money.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticsFragment : Fragment() {

    // ─── ViewModel ─────────────────────────────────────────
    private val viewModel: StatisticsViewModel by viewModels()

    // ─── UI refs ───────────────────────────────────────────
    private lateinit var expenseChart: ExpenseChartView
    private lateinit var dateText:     TextView
    private lateinit var list:         RecyclerView
    private lateinit var btnPrev:      ImageView
    private lateinit var btnNext:      ImageView
    private lateinit var btnBack:      ImageView

    // ─── Formatting & state ────────────────────────────────
    private val fmtDate  = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val fmtMoney = NumberFormat.getCurrencyInstance(Locale("kk","KZ"))
    private val calendar = Calendar.getInstance()

    // ─── Adapter ───────────────────────────────────────────
    private val adapter = CategoryStatsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_statistics, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // bind views
        expenseChart = view.findViewById(R.id.expense_chart)
        dateText     = view.findViewById(R.id.date_text)
        list         = view.findViewById(R.id.category_list)
        btnPrev      = view.findViewById(R.id.btn_prev_date)
        btnNext      = view.findViewById(R.id.btn_next_date)
        btnBack      = view.findViewById(R.id.btn_back)

        setupRecycler()
        setupListeners()
        refreshDateDisplay()
        loadDataForDate()
    }

    private fun setupRecycler() {
        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter        = adapter
    }

    private fun setupListeners() {
        btnPrev.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            refreshDateDisplay()
            loadDataForDate()
        }
        btnNext.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, +1)
            refreshDateDisplay()
            loadDataForDate()
        }
        btnBack.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun refreshDateDisplay() {
        dateText.text = fmtDate.format(calendar.time).uppercase()
    }

    private fun loadDataForDate() {
        // 1) per-category stats
        viewModel.getCategoriesByDate(calendar.time)
            .observe(viewLifecycleOwner) { stats: List<CategoryStats> ->
                adapter.submitList(stats)

                expenseChart.clearCategories()
                for (s in stats) {
                    expenseChart.addCategory(
                        s.name,
                        s.amount.toFloat(),
                        s.color
                    )
                }
            }

        // 2) total expense
        viewModel.getTotalByDate(calendar.time)
            .observe(viewLifecycleOwner) { total: Double ->
                expenseChart.setCenterText(
                    "Expense",
                    "₸${String.format("%,.2f", total)}"
                )
            }
    }

    // ─── RecyclerView Adapter & model ──────────────────────
    inner class CategoryStatsAdapter
        : RecyclerView.Adapter<CategoryStatsAdapter.VH>() {

        private var items = emptyList<CategoryStats>()

        fun submitList(newItems: List<CategoryStats>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_caterogy_stats, parent, false)
        )

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val stat = items[position]
            holder.categoryName.text      = stat.name
            holder.categoryAmount.text    = fmtMoney.format(stat.amount)
            holder.categoryProgress.progress = stat.progress

            // tint the ProgressBar
            holder.categoryProgress.progressDrawable.setColorFilter(
                stat.color, android.graphics.PorterDuff.Mode.SRC_IN
            )
        }

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val categoryName     : TextView    = view.findViewById(R.id.category_name)
            val categoryAmount   : TextView    = view.findViewById(R.id.category_amount)
            val categoryProgress : ProgressBar = view.findViewById(R.id.category_progress)
        }
    }

    // ─── Data model (used by ViewModel & Repository) ───────
    data class CategoryStats(
        val name: String,
        val amount: Double,
        val color: Int,
        val progress: Int
    )

    companion object {
        @JvmStatic fun newInstance() = StatisticsFragment()
    }
}
