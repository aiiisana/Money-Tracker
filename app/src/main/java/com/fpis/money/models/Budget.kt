package com.fpis.money.models
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val amount: Double,
    val spent: Double,
    val color: String,
)
{
    val remaining: Double
        get() = amount - spent

    val isOverspending: Boolean
        get() = spent > amount
    val percentage: Int
        get() = if (amount > 0) ((spent / amount) * 100).toInt() else 0
}