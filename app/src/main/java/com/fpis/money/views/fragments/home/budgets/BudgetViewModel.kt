package com.fpis.money.views.fragments.home.budgets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fpis.money.models.Budget
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.utils.database.repo.BudgetRepository
import com.fpis.money.utils.database.TransactionDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BudgetRepository
    val budgets: LiveData<List<Budget>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = BudgetRepository(db.budgetDao(), db.transactionDao())
        budgets = repository.getAllBudgets().asLiveData()
    }

    fun getBudgetById(budgetId: String): LiveData<Budget?> {
        val result = MutableLiveData<Budget?>()
        viewModelScope.launch {
            val budget = repository.getBudgetById(budgetId)
            result.postValue(budget)
        }
        return result
    }

    fun addBudget(budget: Budget) {
        viewModelScope.launch {
            repository.insertBudget(budget)
        }
    }

    fun updateBudget(budget: Budget) {
        viewModelScope.launch {
            repository.updateBudget(budget)
        }
    }

    fun deleteBudget(budgetId: String) {
        viewModelScope.launch {
            repository.deleteBudget(budgetId)
        }
    }
//    fun updateBudgetSpending() {
//        if (transactionDao == null) return
//
//        viewModelScope.launch {
//            val budgets = budgetDao.getAllBudgets().first()
//
//            for (budget in budgets) {
//                val spent = transactionDao.getSpentAmountByCategory(budget.category).first() ?: 0.0
//                val updatedBudget = budget.copy(spent = spent)
//                budgetDao.updateBudget(updatedBudget)
//            }
//        }
//    }
}

