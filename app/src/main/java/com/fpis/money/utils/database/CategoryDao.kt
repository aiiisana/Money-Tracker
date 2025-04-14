package com.fpis.money.utils.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fpis.money.models.Category
import com.fpis.money.models.Subcategory

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category)

    @Insert
    suspend fun insertSubcategory(subcategory: Subcategory)

    @Query("SELECT * FROM categories WHERE isIncomeCategory = :isIncome")
    suspend fun getCategoriesByType(isIncome: Boolean): List<Category>

    @Query("SELECT * FROM subcategories WHERE categoryId = :categoryId")
    suspend fun getSubcategories(categoryId: Int): List<Subcategory>

    @Query("SELECT * FROM categories WHERE isDefault = 1 AND isIncomeCategory = :isIncome")
    suspend fun getDefaultCategories(isIncome: Boolean): List<Category>
}