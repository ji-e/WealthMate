package com.jie.wealthmate

import com.jie.wealthmate.di.commonModule
import com.jie.wealthmate.di.platformModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        // iOS에서는 Context가 필요 없으므로 androidContext를 호출하지 않습니다.
        modules(commonModule, platformModule)
    }
}