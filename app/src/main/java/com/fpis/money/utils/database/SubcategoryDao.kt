package com.fpis.money.utils.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fpis.money.models.Subcategory

@Dao
interface SubcategoryDao {
    @Insert
    suspend fun insert(subcategory: Subcategory): Long

    @Update
    suspend fun update(subcategory: Subcategory)

    @Delete
    suspend fun delete(subcategory: Subcategory)

//    @Query("SELECT * FROM subcategories WHERE parentCategoryId = :categoryId")
//    suspend fun getSubcategoriesByCategory(categoryId: Int): List<Subcategory>
//
//    @Query("DELETE FROM subcategories WHERE parentCategoryId = :categoryId")
//    suspend fun deleteSubcategoriesByCategory(categoryId: Int)
}