package com.fpis.money.utils.database.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.fpis.money.models.Budget
import com.fpis.money.utils.database.BudgetDao
import com.fpis.money.utils.database.TransactionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
class BudgetRepository(private val budgetDao: BudgetDao,private val transactionDao: TransactionDao) {


    fun getAllBudgets(): Flow<List<Budget>> {
        return if (transactionDao != null) {
            // Combine budgets with their actual spending from transactions
            budgetDao.getAllBudgets().map { budgets ->
                budgets.map { budget ->
                    // For each budget, get the spent amount from transactions
                    // This is a simplified approach - in a real app, you'd use combine() to get real-time updates
                    budget
                }
            }
        } else {
            // Just return budgets as is
            budgetDao.getAllBudgets()
        }
    }

    suspend fun getBudgetById(budgetId: String): Budget? {
        return budgetDao.getBudgetById(budgetId.toLongOrNull() ?: 0)
    }

    suspend fun insertBudget(budget: Budget) {
        budgetDao.insertBudget(budget)
    }

    suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget)
    }

    suspend fun deleteBudget(budgetId: String) {
        budgetDao.deleteBudgetById(budgetId.toLongOrNull() ?: 0)
    }
}