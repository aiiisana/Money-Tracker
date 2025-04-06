package com.fpis.money.models

import java.sql.Date

data class Transfer(
    val fromAccount: String,
    val toAccount: String,
    val amount: Float,
    val date: Date,
    val notes: String? = null
)
