package com.fpis.money.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.roundToInt

@Entity(tableName = "budget_categories")
data class BudgetCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val amount: Double,
    val spent: Double,
    val color: String
) {
    val progress: Int
        get() = if (amount == 0.0) 0 else ((spent / amount) * 100).roundToInt()
}
