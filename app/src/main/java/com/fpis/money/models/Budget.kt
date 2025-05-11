package com.fpis.money.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "", // Added for user association
    val category: String,
    val amount: Double,
    val spent: Double = 0.0,
    val color: String = "#FFFFFF", // Default color
    val iconRes: Int,
    val colorRes: Int,
    val notificationThreshold: Int = 80,
    val timePeriod: String = "monthly", // "monthly", "weekly", "daily"
    val notificationSent: Boolean = false,
    val isSpendingLimit: Boolean = false
) {
    val remaining: Double
        get() = amount - spent

    val isOverspending: Boolean
        get() = spent > amount

    val percentage: Int
        get() = if (amount > 0) ((spent / amount) * 100).toInt() else 0

    fun shouldNotify(): Boolean =
        !notificationSent && percentage >= notificationThreshold && !isOverspending

    // Added properties that were in SpendingLimit
    val isLimitExceeded: Boolean
        get() = spent >= amount
}