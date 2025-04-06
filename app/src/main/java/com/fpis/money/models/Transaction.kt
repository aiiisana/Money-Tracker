package com.fpis.money.models

import java.sql.Date

data class Transaction(
    val type: String,
    val paymentMethod: String,
    val date: Date,
    val amount: Float,
    val category: String,
    val subCategory: String,
    val notes: String
)