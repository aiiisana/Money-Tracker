package com.fpis.money.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transfers")
data class Transfer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromAccount: String,
    val toAccount: String,
    val amount: Float,
    val date: Long,
    val notes: String? = null
)
