package com.fpis.money.models

sealed class TransactionItem {
    data class RecordItem(val record: Transaction) : TransactionItem()
    data class TransferItem(val transfer: Transfer) : TransactionItem()
}