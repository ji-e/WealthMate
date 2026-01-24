package com.jie.wealthmate

import android.app.Application
import com.jie.wealthmate.di.commonModule
import com.jie.wealthmate.di.databaseModule
import com.jie.wealthmate.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(commonModule, platformModule, databaseModule)
        }
    }
}