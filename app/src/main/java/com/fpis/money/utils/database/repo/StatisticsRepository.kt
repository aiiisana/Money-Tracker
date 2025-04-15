package com.fpis.money.utils.database.repo

import com.fpis.money.utils.database.TransactionDao
import com.fpis.money.views.fragments.home.stats.StatisticsFragment.CategoryStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date

class StatisticsRepository(private val transactionDao: TransactionDao) {

    fun getExpensesByDate(date: Date): Flow<List<CategoryStats>> {
        val startOfDay = getStartOfDay(date)
        val endOfDay = getEndOfDay(date)

        return transactionDao.getTransactionsByDateRange(startOfDay.timeInMillis, endOfDay.timeInMillis)
            .map { transactions ->
                // Group transactions by category
                val categoryMap = transactions
                    .filter { it.type == "expense" } // Only include expenses
                    .groupBy { it.category }
                    .mapValues { entry ->
                        entry.value.sumOf { it.amount.toDouble() }
                    }

                // Convert to CategoryStats objects
                val totalExpense = categoryMap.values.sum()

                categoryMap.map { (category, amount) ->
                    CategoryStats(
                        name = category,
                        amount = amount,
                        color = getCategoryColor(category),
                        progress = if (totalExpense > 0) ((amount / totalExpense) * 100).toInt() else 0
                    )
                }
            }
    }

    fun getTotalExpenseByDate(date: Date): Flow<Double> {
        val startOfDay = getStartOfDay(date)
        val endOfDay = getEndOfDay(date)

        return transactionDao.getTransactionsByDateRange(startOfDay.timeInMillis, endOfDay.timeInMillis)
            .map { transactions ->
                transactions
                    .filter { it.type == "expense" }
                    .sumOf { it.amount.toDouble() }
            }
    }

    private fun getStartOfDay(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    private fun getEndOfDay(date: Date): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar
    }

    private fun getCategoryColor(category: String): Int {
        return when (category) {
            "Food & Drink" -> android.graphics.Color.parseColor("#FF9366")
            "Entertainment" -> android.graphics.Color.parseColor("#66FFA3")
            "Transport" -> android.graphics.Color.parseColor("#3DB9FF")
            "Shopping" -> android.graphics.Color.parseColor("#FFD966")
            "Health" -> android.graphics.Color.parseColor("#FF66A3")
            else -> android.graphics.Color.parseColor("#66D9FF")
        }
    }
}