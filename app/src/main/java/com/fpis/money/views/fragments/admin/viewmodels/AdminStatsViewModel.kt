package com.fpis.money.viewmodels.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.HashMap

class AdminStatsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    data class StatsData(
        val totalUsers: Int = 0,
        val activeUsers: Int = 0,
        val totalTransactions: Int = 0,
        val avgTransactionsPerUser: Double = 0.0,
        val totalIncome: Double = 0.0,
        val totalExpenses: Double = 0.0,
        val transactionsByType: Map<String, Int> = emptyMap(),
        val transactionsByDay: Map<String, Int> = emptyMap()
    )

    private val _stats = MutableStateFlow<StatsData?>(null)
    val stats: StateFlow<StatsData?> = _stats.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val usersSnapshot = firestore.collection("users").get().await()
                val totalUsers = usersSnapshot.size()
                val activeUsers = usersSnapshot.count { it.getBoolean("isActive") ?: false }

                val calendar = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -30)
                }
                val transactionsSnapshot = firestore.collection("transactions")
                    .whereGreaterThanOrEqualTo("date", calendar.timeInMillis)
                    .get()
                    .await()

                val transactions = transactionsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject<Transaction>()?.copy(id = doc.id)
                }

                val totalTransactions = transactions.size
                val avgTransactions = if (totalUsers > 0) totalTransactions.toDouble() / totalUsers else 0.0

                val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
                val totalExpenses = transactions.filter { it.type == "expense" }.sumOf { it.amount }

                val transactionsByType = mapOf(
                    "expense" to transactions.count { it.type == "expense" },
                    "income" to transactions.count { it.type == "income" }
                )

                val dayFormat = java.text.SimpleDateFormat("EEE", Locale.getDefault())
                val transactionsByDay = HashMap<String, Int>().apply {
                    for (i in 6 downTo 0) {
                        calendar.time = Date() // Reset to today
                        calendar.add(Calendar.DAY_OF_YEAR, -i)
                        val day = dayFormat.format(calendar.time)
                        put(day, 0)
                    }

                    transactions.groupBy { transaction ->
                        calendar.timeInMillis = transaction.date
                        dayFormat.format(calendar.time)
                    }.forEach { (day, dayTransactions) ->
                        put(day, dayTransactions.size)
                    }
                }

                _stats.value = StatsData(
                    totalUsers = totalUsers,
                    activeUsers = activeUsers,
                    totalTransactions = totalTransactions,
                    avgTransactionsPerUser = avgTransactions,
                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses,
                    transactionsByType = transactionsByType,
                    transactionsByDay = transactionsByDay
                )
            } catch (e: Exception) {
                _error.value = "Failed to load stats: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    data class Transaction(
        val id: String = "",
        val type: String = "",
        val amount: Double = 0.0,
        val date: Long = 0L
    )
}