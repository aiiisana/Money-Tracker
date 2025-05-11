package com.fpis.money.viewmodels.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
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

                // Load all data in parallel
                val usersDeferred = firestore.collection("users").get().await()
                val transactionsDeferred = firestore.collection("transactions")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .limit(1000)
                    .get()
                    .await()

                // Process users
                val totalUsers = usersDeferred.size()
                val activeUsers = usersDeferred.count { it.getBoolean("isActive") ?: false }

                // Process transactions
                val transactions = transactionsDeferred.documents.mapNotNull { it.toObject<Transaction>() }
                val totalTransactions = transactions.size
                val avgTransactions = if (totalUsers > 0) totalTransactions.toDouble() / totalUsers else 0.0

                // Financial stats
                val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
                val totalExpenses = transactions.filter { it.type == "expense" }.sumOf { it.amount }

                // Transactions by type
                val transactionsByType = mapOf(
                    "expense" to transactions.count { it.type == "expense" },
                    "income" to transactions.count { it.type == "income" }
                )

                // Transactions by day (last 7 days)
                val calendar = Calendar.getInstance()
                val dayFormat = java.text.SimpleDateFormat("EEE", Locale.getDefault())
                val transactionsByDay = HashMap<String, Int>().apply {
                    for (i in 6 downTo 0) {
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                        val day = dayFormat.format(calendar.time)
                        put(day, 0) // Initialize all days
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
}

data class Transaction(
    val type: String = "",
    val amount: Double = 0.0,
    val date: Long = 0L
)