package com.fpis.money.utils.database.repo

import com.fpis.money.R
import com.fpis.money.models.Category
import com.fpis.money.models.Subcategory
import com.fpis.money.utils.database.CategoryDao

class CategoryRepository(private val categoryDao: CategoryDao) {
    private val defaultExpenseCategories = listOf(
        Category(name = "Food & Drink", iconRes = R.drawable.ic_food_drink, colorRes = R.color.white, isDefault = true),
        Category(name = "Shopping", iconRes = R.drawable.ic_shopping, colorRes = R.color.white, isDefault = true),
        Category(name = "Health", iconRes = R.drawable.health, colorRes = R.color.white, isDefault = true),
        Category(name = "Transport", iconRes = R.drawable.ic_transport, colorRes = R.color.white, isDefault = true),
        Category(name = "Interest", iconRes = R.drawable.ic_interest, colorRes = R.color.white, isDefault = true),
        Category(name = "Life & Event", iconRes = R.drawable.ic_event, colorRes = R.color.green, isDefault = true)
    )

    private val defaultIncomeCategories = listOf(
        Category(name = "Income", iconRes = R.drawable.ic_debit_card, isIncomeCategory = true, colorRes = R.color.green, isDefault = true),
    )

    suspend fun initializeDefaultCategories() {
        if (categoryDao.getDefaultCategories(false).isEmpty()) {
            defaultExpenseCategories.forEach { categoryDao.insert(it) }
        }
        if (categoryDao.getDefaultCategories(true).isEmpty()) {
            defaultIncomeCategories.forEach { categoryDao.insert(it) }
        }
    }

    suspend fun getCategories(isIncome: Boolean): List<Category> {
        return categoryDao.getCategoriesByType(isIncome)
    }

    suspend fun addCustomCategory(category: Category) {
        categoryDao.insert(category)
    }


    private val defaultSubcategories = mapOf(
        4 to listOf("Taxi", "Public Transport"),
        1 to listOf("Restaurants", "Groceries"),
    )

    suspend fun initializeDefaultSubcategories() {
        if (categoryDao.getDefaultSubcategories().isEmpty()) {
            defaultSubcategories.forEach { (categoryId, subcategories) ->
                subcategories.forEach { subcategoryName ->
                    categoryDao.insertSubcategory(
                        Subcategory(
                            categoryId = categoryId,
                            name = subcategoryName,
                            isDefault = true
                        )
                    )
                }
            }
        }
    }

    suspend fun addCustomSubcategory(categoryId: Int, name: String) {
        categoryDao.insertSubcategory(Subcategory(categoryId = categoryId, name = name))
    }

    suspend fun getSubcategories(categoryId: Int): List<Subcategory> {
        return categoryDao.getSubcategoriesByCategory(categoryId)
    }

    suspend fun deleteSubcategory(subcategoryId: Int) {
        categoryDao.deleteCustomSubcategory(subcategoryId)
    }
}