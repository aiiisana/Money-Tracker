package com.fpis.money.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val paymentMethod: String,
    val date: Long,
    val amount: Float,
    val category: String,
    val subCategory: String,
    val notes: String,
    val iconRes: Int? = null,
    val colorRes: Int? = null
)