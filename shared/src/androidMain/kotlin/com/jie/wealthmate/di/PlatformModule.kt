package com.jie.wealthmate.di

import com.jie.wealthmate.createSettings
import com.jie.wealthmate.database.DatabaseBuilder
import com.jie.wealthmate.database.DatabaseManager
import com.russhwolf.settings.Settings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseBuilder> { DatabaseBuilder(androidContext()) }
    single { DatabaseManager(androidContext(), get(), get()) }
    single<Settings> { createSettings(androidContext()) }
}