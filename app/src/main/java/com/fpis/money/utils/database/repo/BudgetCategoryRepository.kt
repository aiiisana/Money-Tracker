package com.fpis.money.utils.database.repo

import com.fpis.money.models.BudgetCategory
import com.fpis.money.utils.database.BudgetCategoryDao
import kotlinx.coroutines.flow.Flow


class BudgetCategoryRepository(private val budgetcaterogydao: BudgetCategoryDao) {
    fun getAll(): Flow<List<BudgetCategory>> =  budgetcaterogydao.getAll()
    suspend fun insert(category: BudgetCategory) =  budgetcaterogydao.insert(category)
    suspend fun update(category: BudgetCategory) =  budgetcaterogydao.update(category)
    suspend fun delete(category: BudgetCategory) =  budgetcaterogydao.delete(category)
}
