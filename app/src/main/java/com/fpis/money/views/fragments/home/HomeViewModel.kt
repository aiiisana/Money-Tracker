package com.fpis.money.views.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Date

data class BudgetData(
    val total: String,
    val spent: String,
    val remaining: String,
    val spentPercentage: Float // Add percentage for progress bar
)

data class Category(
    val id: String,
    val name: String,
    val spent: String,
    val color: String,
    val progress: Float // Add progress percentage for circular indicators
)

class HomeViewModel : ViewModel() {

    private val _totalBalance = MutableLiveData<String>()
    val totalBalance: LiveData<String> = _totalBalance

    private val _budgetData = MutableLiveData<BudgetData>()
    val budgetData: LiveData<BudgetData> = _budgetData

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    init {
        // Load initial data
        loadInitialData()
    }

    private fun loadInitialData() {
        // In a real app, you would load this data from a repository or API
        _totalBalance.value = "760,000.00"

        val totalBudget = 760000.00
        val spentBudget = 277000.00
        val spentPercentage = (spentBudget / totalBudget) * 100

        _budgetData.value = BudgetData(
            total = "760,000.00",
            spent = "277,000",
            remaining = "483,000.00",
            spentPercentage = spentPercentage.toFloat()
        )

        // Calculate progress percentages based on budget allocation
        val entertainmentProgress = (156500.0 / totalBudget) * 100
        val foodProgress = (80000.0 / totalBudget) * 100
        val transportationProgress = (25000.0 / totalBudget) * 100
        val shoppingProgress = (15500.0 / totalBudget) * 100

        _categories.value = listOf(
            Category(
                id = "1",
                name = "Entertainment",
                spent = "156,500",
                color = "#66FFA3",
                progress = entertainmentProgress.toFloat()
            ),
            Category(
                id = "2",
                name = "Food",
                spent = "80,000",
                color = "#FF9366",
                progress = foodProgress.toFloat()
            ),
            Category(
                id = "3",
                name = "Transportation",
                spent = "25,000",
                color = "#3DB9FF",
                progress = transportationProgress.toFloat()
            ),
            Category(
                id = "4",
                name = "Shopping",
                spent = "15,500",
                color = "#FFD966",
                progress = shoppingProgress.toFloat()
            )
        )
    }

    fun loadDataForDate(date: Date) {
        println("Loading data for: $date")
        // In a real app, you would load data for the specific date
        // For now, just reload the initial data
        loadInitialData()
    }
}