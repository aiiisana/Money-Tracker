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

    private val repository: StatisticsRepository

    init {
        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
        repository = StatisticsRepository(transactionDao)
    }

    fun getExpensesByDate(date: Date): LiveData<List<StatisticsFragment.CategoryStats>> {
        return repository.getExpensesByDate(date).asLiveData()
    }

    fun getTotalExpenseByDate(date: Date): LiveData<Double> {
        return repository.getTotalExpenseByDate(date).asLiveData()
    }
}