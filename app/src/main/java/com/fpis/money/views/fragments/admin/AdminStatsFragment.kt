package com.fpis.money.views.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fpis.money.R
import com.fpis.money.databinding.FragmentAdminStatsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.firestore.FirebaseFirestore

class AdminStatsFragment : Fragment() {
    private var _binding: FragmentAdminStatsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

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
        loadUserStats()
    }

    private fun setupCharts() {
        with(binding.dailyActiveChart) {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(listOf("May 3", "May 9"))
            }

            axisLeft.apply {
                axisMinimum = 0f
                granularity = 1f
                setDrawGridLines(true)
            }
            axisRight.isEnabled = false

            legend.isEnabled = false
        }
    }

    private fun loadUserStats() {
        db.collection("users").get()
            .addOnSuccessListener { snapshot ->
                val totalUsers = snapshot.size()
                val activeUsers = snapshot.count { it.getBoolean("isActive") ?: false }

                binding.tvTotalUsers.text = totalUsers.toString()
                binding.tvActiveUsers.text = activeUsers.toString()

                updateDailyActiveChart(listOf(2f, 1f))
            }
            .addOnFailureListener {
                // Handle error
            }
    }

    private fun updateDailyActiveChart(values: List<Float>) {
        val entries = values.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }

        val dataSet = BarDataSet(entries, "Daily Active Users").apply {
            color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            valueTextSize = 12f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
        }

        binding.dailyActiveChart.data = BarData(dataSet).apply {
            barWidth = 0.4f
        }
        binding.dailyActiveChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}