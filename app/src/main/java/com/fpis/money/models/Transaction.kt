package com.fpis.money.models

data class Transaction(
    val type: String,
    val paymentMethod: String,
    val date: String,
    val amount: Float,
    val category: String
)