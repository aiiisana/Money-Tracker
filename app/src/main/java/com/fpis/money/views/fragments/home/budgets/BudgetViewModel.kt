package com.fpis.money.views.fragments.home.budgets

import android.app.Application
import androidx.lifecycle.*
import com.fpis.money.models.Budget
import com.fpis.money.models.Category
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.utils.database.repo.BudgetRepository
import com.fpis.money.utils.database.repo.CategoryRepository
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val budgetRepo = BudgetRepository(db.budgetDao(), db.transactionDao())
    private val categoryRepo = CategoryRepository(db.categoryDao())

    val budgets: LiveData<List<Budget>> =
        budgetRepo.getAllBudgets().asLiveData()

    // ► Expense‐category list
    private val _expenseCats = MutableLiveData<List<Category>>(emptyList())
    val expenseCategories: LiveData<List<Category>> = _expenseCats

    init {
        viewModelScope.launch {
            // ensure defaults exist
            categoryRepo.initializeDefaultCategories()
            categoryRepo.initializeDefaultSubcategories()
            // load them
            _expenseCats.postValue(categoryRepo.getCategories(isIncome = false))
        }
    }

    fun addBudget(b: Budget)     = viewModelScope.launch { budgetRepo.insertBudget(b) }
    fun updateBudget(b: Budget)  = viewModelScope.launch { budgetRepo.updateBudget(b) }
    fun deleteBudget(id: String) = viewModelScope.launch {
        val longId = id.toLongOrNull()
        longId?.let { budgetRepo.deleteBudget(it) }
    }

    fun getBudgetById(id: String): LiveData<Budget?> {
        val out = MutableLiveData<Budget?>()
        viewModelScope.launch {
            val longId = id.toLongOrNull()
            if (longId != null) {
                val b = budgetRepo.getBudgetById(longId)
                out.postValue(b)
            } else {
                out.postValue(null)
            }
        }
        return out
    }
}