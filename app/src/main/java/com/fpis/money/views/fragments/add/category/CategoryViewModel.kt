package com.fpis.money.views.fragments.add.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fpis.money.models.Category
import com.fpis.money.models.Subcategory
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.utils.database.repo.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CategoryRepository
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    init {
        val dao = AppDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(dao)
    }

    fun loadCategories(isIncome: Boolean) {
        viewModelScope.launch {
            repository.initializeDefaultCategories()
            _categories.value = repository.getCategories(isIncome)
        }
    }

    fun addCustomCategory(category: Category) {
        viewModelScope.launch {
            repository.addCustomCategory(category)
            loadCategories(category.isIncomeCategory)
        }
    }

    private val _subcategories = MutableLiveData<List<Subcategory>>()
    val subcategories: LiveData<List<Subcategory>> = _subcategories

    fun loadSubcategories(categoryId: Int) {
        viewModelScope.launch {
            repository.initializeDefaultSubcategories()
            _subcategories.value = repository.getSubcategories(categoryId)
        }
    }

    fun addCustomSubcategory(categoryId: Int, name: String) {
        viewModelScope.launch {
            repository.addCustomSubcategory(categoryId, name)
            loadSubcategories(categoryId)
        }
    }

    fun deleteSubcategory(subcategoryId: Int, categoryId: Int) {
        viewModelScope.launch {
            repository.deleteSubcategory(subcategoryId)
            loadSubcategories(categoryId)
        }
    }

    fun deleteCategory(categoryId: Int, isIncome: Boolean) {
        viewModelScope.launch {
            repository.deleteCategory(categoryId)
            loadCategories(isIncome)
        }
    }
}