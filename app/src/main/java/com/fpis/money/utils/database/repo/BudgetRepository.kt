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
        // Watch the list of budgets
        return budgetDao.getAllBudgets()
            // whenever budgets change...
            .flatMapLatest { budgets ->
                if (budgets.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    // For each budget, get its spent amount as a Flow<Double?>
                    val updatedFlows = budgets.map { budget ->
                        transactionDao.getSpentAmountByCategory(budget.category)
                            .map { sum ->
                                // Null sum means 0.0
                                val spent = sum ?: 0.0
                                // Copy budget with new spent
                                budget.copy(spent = spent)
                            }
                    }
                    // Combine all those into List<Budget>
                    combine(updatedFlows) { arrayOfBudgets ->
                        arrayOfBudgets.toList()
                    }
                }
            }
            // Whenever we emit a new list, write the updated spent back into the DB
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
