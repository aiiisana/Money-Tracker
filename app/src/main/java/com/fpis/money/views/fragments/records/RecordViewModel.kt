package com.fpis.money.views.fragments.records

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fpis.money.models.Transaction
import com.fpis.money.models.TransactionItem
import com.fpis.money.models.Transfer
import com.fpis.money.utils.database.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val transactionDao = db.transactionDao()
    private val transferDao = db.transferDao()

    val allTransactions: LiveData<List<TransactionItem>> = combine(
        transactionDao.getAllTransactions(),
        transferDao.getAllTransfers()
    ) { records, transfers ->
        val recordItems = records.map { TransactionItem.RecordItem(it) }
        val transferItems = transfers.map { TransactionItem.TransferItem(it) }

        (recordItems + transferItems).sortedByDescending {
            when (it) {
                is TransactionItem.RecordItem -> it.record.date
                is TransactionItem.TransferItem -> it.transfer.date
            }
        }
    }.asLiveData()

    fun deleteRecord(record: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionDao.delete(record)
        }
    }

    fun deleteTransfer(transfer: Transfer) {
        viewModelScope.launch(Dispatchers.IO) {
            transferDao.delete(transfer)
        }
    }
}