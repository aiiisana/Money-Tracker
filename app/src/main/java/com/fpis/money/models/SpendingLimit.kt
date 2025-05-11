package com.fpis.money.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "spending_limits")
data class SpendingLimit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val budgetId: Long? = null, // Reference to a budget if applicable
    val category: String,
    val limitAmount: Double,
    val spentAmount: Double = 0.0,
    val timePeriod: String = "monthly",
    val iconRes: Int,
    val colorRes: Int,
    val notificationThreshold: Int = 80,
    val notificationSent: Boolean = false
) {
    val isLimitExceeded: Boolean
        get() = spentAmount >= limitAmount

    val percentage: Int
        get() = if (limitAmount > 0) ((spentAmount / limitAmount) * 100).toInt() else 0

    fun shouldNotify(): Boolean {
        return !notificationSent && percentage >= notificationThreshold && !isLimitExceeded
    }
}