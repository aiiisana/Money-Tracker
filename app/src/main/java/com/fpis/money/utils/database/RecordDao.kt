package com.fpis.money.utils.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fpis.money.models.Record
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Insert
    suspend fun insert(record: Record)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsSortedByDate(): Flow<List<Record>>
}