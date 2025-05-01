package com.fpis.money.views.fragments.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.fpis.money.models.Budget
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.utils.database.repo.BudgetRepository
import com.fpis.money.utils.database.repo.StatisticsRepository
import com.fpis.money.views.fragments.home.stats.StatisticsFragment.CategoryStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.text.NumberFormat
import java.util.*

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val db         = AppDatabase.getDatabase(app)
    private val budgetRepo = BudgetRepository(db.budgetDao(), db.transactionDao())
    private val statsRepo  = StatisticsRepository(db.transactionDao())

    private val _date = MutableStateFlow(Date())

    // ← Use a block here instead of '=' because assignments are not expressions
    fun setDate(d: Date) {
        _date.value = d
    }

    val totalExpense: LiveData<String> = _date
        .flatMapLatest { statsRepo.getTotalExpenseByDate(it) }
        .map { NumberFormat.getNumberInstance(Locale.US).format(it) }
        .map { "₸$it" }
        .asLiveData()

    val categoryStats: LiveData<List<CategoryStats>> = _date
        .flatMapLatest { statsRepo.getExpensesByDate(it) }
        .map { list ->
            val total = list.sumOf { it.amount }
            list.map {
                it.copy(progress = if (total == 0.0) 0 else ((it.amount / total) * 100).toInt())
            }
        }
        .asLiveData()

    data class Summary(val total: Double, val spent: Double) {
        val remaining get() = total - spent
        val percentage get() = if (total == 0.0) 0 else (spent / total * 100).toInt()
    }

    val budgetSummary: LiveData<Summary> = budgetRepo.getAllBudgets()
        .map { list ->
            val t = list.sumOf { it.amount }
            val s = list.sumOf { it.spent }
            Summary(t, s)
        }
        .asLiveData()
    val budgets: LiveData<List<Budget>> =
        budgetRepo.getAllBudgets()
            .asLiveData()
}
