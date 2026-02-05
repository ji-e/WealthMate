package com.jie.wealthmate

import android.app.Application
import com.jie.wealthmate.di.commonModule
import com.jie.wealthmate.di.databaseModule
import com.jie.wealthmate.di.platformModule
import com.russhwolf.settings.BuildConfig
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog()) // 디버그 모드에서만 로그 활성화
        }

        startKoin {
            androidContext(this@MainApplication)
            modules(commonModule, platformModule, databaseModule)
        }
    }
}