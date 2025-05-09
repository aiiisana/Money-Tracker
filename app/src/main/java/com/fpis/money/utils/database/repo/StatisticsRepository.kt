package com.fpis.money.utils.database.repo

import com.fpis.money.models.Transaction
import com.fpis.money.utils.database.TransactionDao
import com.fpis.money.views.fragments.home.stats.StatisticsFragment.CategoryStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date

class StatisticsRepository(private val transactionDao: TransactionDao) {

    fun getExpensesByDate(date: Date): Flow<List<CategoryStats>> {
        val start = getStartOfDay(date).timeInMillis
        val end   = getEndOfDay(date).timeInMillis

        return transactionDao.getTransactionsByDateRange(start, end)
            .map { transactions ->
                val expenses    = transactions.filter { it.type == "expense" }
                val totalAmount = expenses.sumOf { it.amount.toDouble() }
                expenses.map { tx ->
                    CategoryStats(
                        name     = tx.category,
                        amount   = tx.amount.toDouble(),
                        color    = getCategoryColor(tx.category),
                        progress = if (totalAmount > 0)
                            ((tx.amount / totalAmount) * 100).toInt()
                        else 0
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

    fun getTransactionsByDate(date: Date): Flow<List<Transaction>> {
        val start = getStartOfDay(date).timeInMillis
        val end   = getEndOfDay(date).timeInMillis
        return transactionDao.getTransactionsByDateRange(start, end)
            .map { it.filter { tx -> tx.type=="expense" } }
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