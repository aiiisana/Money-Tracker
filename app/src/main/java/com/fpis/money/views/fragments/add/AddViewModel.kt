package com.fpis.money.views.fragments.add

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fpis.money.models.Transaction
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.utils.database.DatabaseHelper
import kotlinx.coroutines.launch

class AddViewModel(application: Application) : AndroidViewModel(application as Application) {

    private val transactionDao = AppDatabase.getDatabase(application).transactionDao()

    fun saveTransaction(type: String, amount: String, category: String, date: Long, notes: String, paymentMethod: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        val newTransaction = Transaction(
            type = type,
            paymentMethod = paymentMethod,
            date = date,
            amount = amount.toFloat(),
            category = category,
            subCategory = "",
            notes = notes
        )

        viewModelScope.launch {
            transactionDao.insert(newTransaction)
            Log.d("AddViewModel", "Inserted transaction: ${newTransaction}")
            result.postValue(true)
        }

        return result
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