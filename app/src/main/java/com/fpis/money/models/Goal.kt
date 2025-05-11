package com.fpis.money.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fpis.money.R

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: Long,              // timestamp in millis
    val category: String? = null,      // optional filter
    val iconRes: Int = R.drawable.ic_goal,
    val colorRes: Int = R.color.purple_500,
    val isCompleted: Boolean = false
)
