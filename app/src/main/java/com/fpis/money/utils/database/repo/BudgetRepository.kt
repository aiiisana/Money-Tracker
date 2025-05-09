package com.fpis.money.utils.database.repo

import com.fpis.money.models.Budget
import com.fpis.money.utils.database.BudgetDao
import com.fpis.money.utils.database.TransactionDao
import kotlinx.coroutines.flow.*

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao
) {

    /**
     * Emits budgets with fresh `spent` totals.
     * Also writes those back to the DB so they’re persisted.
     */
    fun getAllBudgets(): Flow<List<Budget>> {
        return budgetDao.getAllBudgets()
            .flatMapLatest { budgets ->
                if (budgets.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val updatedFlows = budgets.map { budget ->
                        transactionDao.getSpentAmountByCategory(budget.category)
                            .map { sum ->
                                val spent = sum ?: 0.0
                                budget.copy(spent = spent)
                            }
                    }
                    combine(updatedFlows) { arrayOfBudgets ->
                        arrayOfBudgets.toList()
                    }
                }
            }
            .onEach { updatedBudgets ->
                updatedBudgets.forEach { b ->
                    budgetDao.updateBudget(b)
                }
            }
    }

    suspend fun getBudgetById(budgetId: Long): Budget? =
        budgetDao.getBudgetById(budgetId)

    suspend fun insertBudget(budget: Budget) =
        budgetDao.insertBudget(budget)

    suspend fun updateBudget(budget: Budget) =
        budgetDao.updateBudget(budget)

    suspend fun deleteBudget(budgetId: Long) =
        budgetDao.deleteBudgetById(budgetId)
}
