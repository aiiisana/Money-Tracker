package com.fpis.money

import advanced.lab.chatlibrary.chatLibraryModule
import android.app.Application
import com.fpis.money.utils.koinModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MoneyTracker : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MoneyTracker)
            modules(koinModule, chatLibraryModule)
        }
    }
}