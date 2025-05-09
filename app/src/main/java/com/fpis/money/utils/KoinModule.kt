package com.fpis.money.utils

import com.fpis.money.utils.broadcast.WifiStateReceiver
import com.fpis.money.utils.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    factory { WifiStateReceiver() }
}