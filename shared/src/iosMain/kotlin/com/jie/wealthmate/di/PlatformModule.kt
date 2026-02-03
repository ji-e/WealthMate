package com.jie.wealthmate.di

import com.jie.wealthmate.database.DatabaseBuilder
import com.jie.wealthmate.database.DatabaseManager
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseBuilder> { DatabaseBuilder() }
    single { DatabaseManager(get()) }
}