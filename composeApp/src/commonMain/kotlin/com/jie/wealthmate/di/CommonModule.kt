package com.jie.wealthmate.di

import com.jie.wealthmate.feature.asset.AssetScreenModel
import com.jie.wealthmate.feature.calendar.CalendarScreenModel
import com.jie.wealthmate.feature.calendar.component.addHistory.AddHistoryScreenModel
import com.jie.wealthmate.feature.home.HomeScreenModel
import com.jie.wealthmate.feature.menu.MenuScreenModel
import com.jie.wealthmate.feature.menu.categorySetting.addCategory.AddCategoryScreenModel
import com.jie.wealthmate.feature.menu.categorySetting.categorySetting.CategorySettingScreenModel
import com.jie.wealthmate.feature.menu.categorySetting.modifyCategory.ModifyCategoryScreenModel
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.CategoryRepositoryImpl
import org.koin.dsl.module

val commonModule = module {

    single<CategoryRepository> {
        CategoryRepositoryImpl(databaseDriverFactory = get())
    }

    // 여기에 ScreenModel, Repository 등 다른 공통 클래스들도 추가할 수 있습니다.
    // 홈
    factory { HomeScreenModel() }

    // 캘린더
    factory { CalendarScreenModel() }
    factory { AddHistoryScreenModel() }

    // 자산
    factory { AssetScreenModel() }

    // 메뉴
    factory { MenuScreenModel() }
    factory { AddCategoryScreenModel(get()) }
    factory { ModifyCategoryScreenModel(get()) }
    factory { CategorySettingScreenModel(get()) }
}