package com.fpis.money.utils.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.fpis.money.models.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE type NOT IN ('transfer_in', 'transfer_out')")
    fun getAllTransactionsWithFavorite(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type NOT IN ('transfer_in', 'transfer_out') AND isFavorite = 0")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE type = :transactionType")
    suspend fun getTransactionsByType(transactionType: String): List<Transaction>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate AND isFavorite = 0")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'expense' AND category = :category")
    fun getSpentAmountByCategory(category: String): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE isFavorite = 1")
    fun getFavoriteTransactions(): LiveData<List<Transaction>>

}