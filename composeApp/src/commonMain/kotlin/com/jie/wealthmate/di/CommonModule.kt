package com.jie.wealthmate.di

import com.jie.wealthmate.database.Database
import com.jie.wealthmate.feature.home.HomeScreenModel
import org.koin.dsl.module

val commonModule = module {
    /**
     * Database의 싱글턴 인스턴스를 제공합니다.
     * DatabaseDriverFactory는 각 플랫폼 모듈에서 주입될 것입니다.
     */
    single<Database> {
        Database(databaseDriverFactory = get())
    }

    // 여기에 ScreenModel, Repository 등 다른 공통 클래스들도 추가할 수 있습니다.
     factory { HomeScreenModel(database = get()) }
}