package com.fpis.money.utils.database

import android.content.Context
import androidx.room.Room

object DatabaseHelper {

    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (database == null) {
            database = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "money_tracker_db"
            ).build()
        }
        return database!!
    }
}