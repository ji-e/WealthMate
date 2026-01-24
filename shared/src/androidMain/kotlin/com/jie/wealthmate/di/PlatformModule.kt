package com.jie.wealthmate.di

import com.jie.wealthmate.database.DatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseBuilder> { DatabaseBuilder(androidContext()) }
}