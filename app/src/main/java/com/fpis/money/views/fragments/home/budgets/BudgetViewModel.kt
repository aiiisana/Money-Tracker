package com.fpis.money.views.fragments.home.budgets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BudgetViewModel : ViewModel() {

    private val _budgets = MutableLiveData<List<Budget>>()
    val budgets: LiveData<List<Budget>> = _budgets

    init {
        // In a real app, you would load this from a repository
        loadBudgets()
    }

    private fun loadBudgets() {
        // Sample data for now
        _budgets.value = listOf(
            Budget(
                id = "1",
                category = "Entertainment",
                amount = 100000.0,
                spent = 54503.0,
                color = "#66FFA3",
                percentage = 19
            ),
            Budget(
                id = "2",
                category = "Food",
                amount = 50000.0,
                spent = 54503.0,
                color = "#FF9366",
                percentage = 123
            ),
            Budget(
                id = "3",
                category = "Transport",
                amount = 10000.0,
                spent = 0.0,
                color = "#3DB9FF",
                percentage = 0
            )
        )
    }

    fun deleteBudget(budgetId: String) {
        val currentList = _budgets.value ?: return
        _budgets.value = currentList.filter { it.id != budgetId }
    }
    // Add these methods to your BudgetViewModel class

    fun getBudgetById(budgetId: String): Budget? {
        return _budgets.value?.find { it.id == budgetId }
    }

    fun addBudget(budget: Budget) {
        val currentList = _budgets.value?.toMutableList() ?: mutableListOf()
        currentList.add(budget)
        _budgets.value = currentList
    }

    fun updateBudget(budget: Budget) {
        val currentList = _budgets.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.id == budget.id }
        if (index != -1) {
            currentList[index] = budget
            _budgets.value = currentList
        }
    }
}