package com.fpis.money.views.fragments.add

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fpis.money.models.Budget
import com.fpis.money.models.Subcategory
import com.fpis.money.models.Transaction
import com.fpis.money.models.Transfer
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.utils.database.DatabaseHelper
import kotlinx.coroutines.launch

class AddViewModel(application: Application) : AndroidViewModel(application as Application) {

    private val transactionDao = AppDatabase.getDatabase(application).transactionDao()
    private val transferDao = AppDatabase.getDatabase(application).transferDao()

    fun saveTransaction(type: String, amount: String, category: String, date: Long, notes: String, paymentMethod: String, subcategory: String, iconRes: Int?, colorRes: Int): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        val newTransaction = Transaction(
            type = type,
            paymentMethod = paymentMethod,
            date = date,
            amount = amount.toFloat(),
            category = category,
            subCategory = subcategory,
            notes = notes,
            iconRes = iconRes,
            colorRes = colorRes,
            isFavorite = false,
            isTemplate = false
        )

        viewModelScope.launch {
            transactionDao.insert(newTransaction)
            Log.d("AddViewModel", "Inserted transaction: ${newTransaction}")
            result.postValue(true)
        }

        return result
    }

    private fun sendBudgetNotification(budget: Budget) {
        val notificationManager = getApplication<Application>()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "budget_alerts",
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Budget limit notifications" }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(getApplication(), "budget_alerts")
            .setSmallIcon(budget.iconRes)
            .setContentTitle("Budget Limit Approaching")
            .setContentText("${budget.category} spending at ${budget.percentage}% (₸${budget.spent}/₸${budget.amount})")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(budget.id.toInt(), notification)
    }

    fun saveTransfer(fromAccount: String, toAccount: String, amount: Float, date: Long, notes: String) {
        val outTransaction = Transaction(
            type = "transfer_out",
            paymentMethod = fromAccount,
            date = date,
            amount = -amount,
            category = "Transfer",
            subCategory = toAccount,
            notes = notes,
            isFavorite = false,
            isTemplate = false
        )

        val inTransaction = Transaction(
            type = "transfer_in",
            paymentMethod = toAccount,
            date = date,
            amount = amount,
            category = "Transfer",
            subCategory = fromAccount,
            notes = notes,
            isFavorite = false,
            isTemplate = false
        )

        val transfer = Transfer(
            fromAccount = fromAccount,
            toAccount = toAccount,
            amount = amount,
            date = date,
            notes = notes,
            isFavorite = false
        )

        viewModelScope.launch {
            val db = AppDatabase.getDatabase(getApplication())
            db.transactionDao().insert(outTransaction)
            db.transactionDao().insert(inTransaction)
            db.transferDao().insertTransfer(transfer)
        }
    }

    fun getFavoriteTransactions(): LiveData<List<Transaction>> {
        return transactionDao.getFavoriteTransactions()
    }

    fun saveAsFavorite(transaction: Transaction) {
        viewModelScope.launch {
            val favorite = transaction.copy(isFavorite = true, isTemplate = true)
            transactionDao.insert(favorite)
        }
    }

    fun getFavoriteTransfers(): LiveData<List<Transfer>> {
        return transferDao.getFavoriteTransfers()
    }

    fun saveAsFavoriteTransfer(transfer: Transfer) {
        viewModelScope.launch {
            val favorite = transfer.copy(isFavorite = true)
            transferDao.insertTransfer(favorite)
        }
    }

    fun deleteFavorite(favorite: Transaction) {
        viewModelScope.launch {
            transactionDao.delete(favorite)
        }
    }

    fun deleteFavoriteTransfer(favorite: Transfer) {
        viewModelScope.launch {
            transferDao.delete(favorite)
        }
    }
}

class AddViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}