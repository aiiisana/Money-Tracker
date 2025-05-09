package com.fpis.money.views.fragments.home.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fpis.money.utils.database.AppDatabase
import com.fpis.money.utils.database.repo.StatisticsRepository
import kotlinx.coroutines.flow.map
import java.util.Date

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = StatisticsRepository(
        AppDatabase.getDatabase(application).transactionDao()
    )

    fun getCategoriesByDate(date: Date) =
        repo.getExpensesByDate(date).asLiveData()

    fun getTotalByDate(date: Date) =
        repo.getTotalExpenseByDate(date).asLiveData()

    // new — raw list of each expense
    fun getTransactionsByDate(date: Date) =
        repo.getTransactionsByDate(date).asLiveData()

}