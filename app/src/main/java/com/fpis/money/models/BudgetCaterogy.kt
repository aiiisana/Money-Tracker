package com.fpis.money.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.roundToInt

@Entity(tableName = "budget_categories")
data class BudgetCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Visible name shown on screen (“Food”, “Entertainment”, …) */
    val name: String,

    /** Planned allocation for the period */
    val amount: Double,

    /** Money already spent */
    val spent: Double,

    /** Hex colour you use for charts and chips (“#FF9366”, “#4285F4”, …) */
    val color: String
) {
    /** 0-100  →  how much of the budget is used */
    val progress: Int
        get() = if (amount == 0.0) 0 else ((spent / amount) * 100).roundToInt()
}
