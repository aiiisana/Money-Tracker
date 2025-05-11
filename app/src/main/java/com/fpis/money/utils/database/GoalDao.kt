package com.fpis.money.utils.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fpis.money.models.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert
    suspend fun insert(goal: Goal)

    @Update
    suspend fun update(goal: Goal)

    @Query("SELECT * FROM goals WHERE isCompleted = 0 ORDER BY targetDate ASC")
    fun getActiveGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE category = :category AND isCompleted = 0")
    fun getGoalsByCategory(category: String): Flow<List<Goal>>
}
