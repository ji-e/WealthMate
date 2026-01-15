package com.jie.wealthmate.di

import com.jie.wealthmate.database.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> {
        DatabaseDriverFactory(context = androidContext())
    }
}