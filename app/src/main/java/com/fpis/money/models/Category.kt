package com.fpis.money.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val iconRes: Int,
    val subcategories: List<String> = emptyList(),
    val isIncomeCategory: Boolean = false,
    val isDefault: Boolean = false
)