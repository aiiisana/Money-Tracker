package com.fpis.money.views.fragments.spendinglimitpackage

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fpis.money.models.Budget
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.utils.database.BudgetDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SpendingLimitsViewModel(application: Application) : AndroidViewModel(application) {
    private val budgetDao = AppDatabase.getDatabase(application).budgetDao()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val allSpendingLimits: LiveData<List<Budget>> = budgetDao.getAllSpendingLimits()

    fun addSpendingLimit(category: String, amount: Double, iconRes: Int, colorRes: Int, threshold: Int = 80) = viewModelScope.launch {
        val newLimit = Budget(
            userId = currentUserId,
            category = category,
            amount = amount,
            spent = 0.0,
            iconRes = iconRes,
            colorRes = colorRes,
            notificationThreshold = threshold,
            timePeriod = "monthly",
            isSpendingLimit = true
        )
        budgetDao.insertBudget(newLimit)
    }

    fun updateSpendingLimit(limit: Budget) = viewModelScope.launch {
        val updatedLimit = limit.copy(notificationSent = false)
        budgetDao.updateBudget(updatedLimit)
    }

    fun deleteSpendingLimit(limit: Budget) = viewModelScope.launch {
        budgetDao.deleteBudgetById(limit.id)
    }

    fun checkAndNotifyLimits() = viewModelScope.launch {
        val limitsNeedingNotification = budgetDao.getBudgetsNeedingNotification()
            .filter { it.isSpendingLimit }

        for (limit in limitsNeedingNotification) {
            sendLimitNotification(limit)
            budgetDao.markNotificationSent(limit.id)
        }
    }

    private fun sendLimitNotification(limit: Budget) {
        val notificationManager = getApplication<Application>()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "spending_limits",
                "Spending Limits",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Spending limit notifications" }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(getApplication(), "spending_limits")
            .setSmallIcon(limit.iconRes)
            .setContentTitle("Spending Limit Alert")
            .setContentText("${limit.category} spending at ${limit.percentage}% (₸${limit.spent.toInt()}/₸${limit.amount.toInt()})")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(limit.id.toInt(), notification)
    }

    fun resetMonthlySpending() = viewModelScope.launch {
        budgetDao.resetSpending("monthly")
    }
}