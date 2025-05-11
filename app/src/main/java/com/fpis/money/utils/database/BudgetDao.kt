package com.fpis.money.utils.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fpis.money.models.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE category = :category LIMIT 1")
    fun getBudgetByCategory(category: String): Flow<Budget?>

    @Query("SELECT * FROM budgets WHERE id = :budgetId")
    suspend fun getBudgetById(budgetId: Long): Budget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Query("DELETE FROM budgets WHERE id = :budgetId")
    suspend fun deleteBudgetById(budgetId: Long)

    @Query("SELECT * FROM budgets WHERE isSpendingLimit = 1")
    fun getAllSpendingLimits(): LiveData<List<Budget>>

    @Query("SELECT * FROM budgets WHERE category = :category AND isSpendingLimit = 1 LIMIT 1")
    suspend fun getSpendingLimitForCategory(category: String): Budget?

    @Query("UPDATE budgets SET notificationSent = 1 WHERE id = :budgetId")
    suspend fun markNotificationSent(budgetId: Long)

    @Query("UPDATE budgets SET spent = 0, notificationSent = 0 WHERE isSpendingLimit = 1 AND timePeriod = :timePeriod")
    suspend fun resetSpending(timePeriod: String)

    @Query("SELECT * FROM budgets WHERE (spent * 100.0 / amount) >= notificationThreshold AND spent <= amount")
    suspend fun getBudgetsNeedingNotification(): List<Budget>

    @Query("SELECT * FROM budgets WHERE isSpendingLimit = 1 AND userId = :userId")
    fun getSpendingLimitsByUser(userId: String): LiveData<List<Budget>>


}