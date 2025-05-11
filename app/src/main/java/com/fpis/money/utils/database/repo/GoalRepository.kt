package com.fpis.money.utils.database.repo

import com.fpis.money.models.Goal
import com.fpis.money.utils.database.GoalDao
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val dao: GoalDao) {
    fun getActiveGoals(): Flow<List<Goal>> = dao.getActiveGoals()
    fun getGoalsByCategory(category: String): Flow<List<Goal>> = dao.getGoalsByCategory(category)
    suspend fun insert(goal: Goal) = dao.insert(goal)
    suspend fun update(goal: Goal) = dao.update(goal)
}
