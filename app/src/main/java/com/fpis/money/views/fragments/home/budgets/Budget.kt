package com.fpis.money.views.fragments.home.budgets

data class Budget(
    val id: String,
    val category: String,
    val amount: Double,
    val spent: Double,
    val color: String,
    val percentage: Int
) {
    val remaining: Double
        get() = amount - spent

    val isOverspending: Boolean
        get() = spent > amount
}