package com.fpis.money.utils.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fpis.money.models.Category
import com.fpis.money.models.Converters
import com.fpis.money.models.Subcategory
import com.fpis.money.models.Transaction
import com.fpis.money.models.Transfer

@Database(entities = [Transaction::class, Transfer::class, Category::class, Subcategory::class], version = 8)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun transferDao(): TransferDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subcategoryDao(): SubcategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

//            context.deleteDatabase("money_tracker_database")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "money_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}