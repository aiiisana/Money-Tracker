package com.fpis.money.views.fragments.home.budgets

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.fpis.money.models.Budget
import com.fpis.money.models.Category
import com.fpis.money.models.SpendingLimit
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.utils.database.repo.BudgetRepository
import com.fpis.money.utils.database.repo.CategoryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun addSpendingLimit(limit: SpendingLimit) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("spending_limits")
            .add(limit)
            .addOnSuccessListener {
                Log.d("Firestore", "Spending limit added")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding limit", e)
            }
    }
}