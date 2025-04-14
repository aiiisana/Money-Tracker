package com.fpis.money.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "subcategories")
data class Subcategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int, // ID родительской категории
    val name: String
)

