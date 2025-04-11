package com.fpis.money.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val date: Long,
    val type: RecordType,
    val accountId: String?, // для дохода/расхода
    val sourceAccountId: String?, // для перевода
    val destinationAccountId: String?, // для перевода
    val description: String?
)
