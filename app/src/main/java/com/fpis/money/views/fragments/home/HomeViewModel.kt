package com.fpis.money.views.fragments.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.utils.database.repo.BudgetRepository
import com.fpis.money.utils.database.repo.StatisticsRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import com.fpis.money.models.Budget
import com.fpis.money.models.BudgetCategory
import com.fpis.money.models.Category


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val budgetRepository: BudgetRepository
    private val statisticsRepository: StatisticsRepository

    private val _totalBalance = MutableLiveData<String>()
    val totalBalance: LiveData<String> = _totalBalance

    private val _budgetData = MutableLiveData<Budget>()
    val budgetData: LiveData<Budget> = _budgetData

    val categories: LiveData<List<BudgetCategory>>

    init {
        val db = AppDatabase.getDatabase(application)
        budgetRepository = BudgetRepository(db.budgetDao(),db.transactionDao())
        statisticsRepository = StatisticsRepository(db.transactionDao())

//        // Initialize with default values
//        _totalBalance.value = "760,000.00"
//
//        _budgetData.value = Budget(
//            category = "Total",
//            amount = 760000.00,
//            spent = 0.00,
//            color = "#4285F4"
//        )



        // Transform budget data to categories
        categories = budgetRepository.getAllBudgets().asLiveData().map { budgets ->
            budgets.map { budget ->
                BudgetCategory(
                    name = budget.category,
                    spent = budget.spent, // ✅ use Double, not formatted String
                    color = budget.color,
                    progress = budget.percentage.toFloat()
                )
            }
        }


        // Load initial data
        loadInitialData()
    }
    private fun loadInitialData() {
        viewModelScope.launch {
            // Calculate total budget and spent amount
            val budgets = budgetRepository.getAllBudgets().asLiveData().value ?: emptyList()

            val totalBudget = budgets.sumOf { it.amount }
            val spentBudget = budgets.sumOf { it.spent }

            // Format for display
            _totalBalance.value = String.format("%,.2f", totalBudget)

            // Create a "total" budget for the progress bar
            _budgetData.value = Budget(
                category = "Total",
                amount = totalBudget,
                spent = spentBudget,
                color = "#4285F4"
            )
        }
    }

    fun loadDataForDate(date: Date) {
        viewModelScope.launch {
            // In a real app, you would load data for the specific date
            // For now, just reload the initial data
            loadInitialData()
        }
    }
}