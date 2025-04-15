package com.fpis.money.utils.database

import androidx.room.*
import com.fpis.money.models.BudgetCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetCategoryDao {
    @Query("SELECT * FROM budget_categories")
    fun getAll(): Flow<List<BudgetCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: BudgetCategory)

    @Update
    suspend fun update(category: BudgetCategory)

    @Delete
    suspend fun delete(category: BudgetCategory)

    @Query("DELETE FROM budget_categories")
    suspend fun deleteAll()
}
