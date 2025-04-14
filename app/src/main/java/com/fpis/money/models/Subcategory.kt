//package com.fpis.money.models
//
//import androidx.room.Entity
//import androidx.room.ForeignKey
//import androidx.room.PrimaryKey
//
//@Entity(
//    tableName = "subcategories",
//    foreignKeys = [ForeignKey(
//        entity = Category::class,
//        parentColumns = ["id"],
//        childColumns = ["categoryId"],
//        onDelete = ForeignKey.CASCADE
//    )]
//)
//data class Subcategory(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    val categoryId: Int,
//    val name: String,
//    val isDefault: Boolean = false
//)
//
