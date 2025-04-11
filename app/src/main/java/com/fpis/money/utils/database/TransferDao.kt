package com.fpis.money.utils.database

import androidx.room.*
import com.fpis.money.models.Transfer
import kotlinx.coroutines.flow.Flow
import com.fpis.money.models.Transaction

@Dao
interface TransferDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: Transfer)

    @Query("SELECT * FROM transfers")
    fun getAllTransfers(): Flow<List<Transfer>>

    @Query("SELECT * FROM transfers WHERE id = :id")
    suspend fun getTransferById(id: Int): Transfer?

    @Update
    suspend fun updateTransfer(transfer: Transfer)

    @Delete
    suspend fun delete(transfer: Transfer)

    @Query("DELETE FROM transfers")
    suspend fun deleteAllTransfers()
}