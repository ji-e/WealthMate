package com.jie.wealthmate.di

import com.jie.wealthmate.feature.asset.AssetScreenModel
import com.jie.wealthmate.feature.calendar.CalendarScreenModel
import com.jie.wealthmate.feature.home.HomeScreenModel
import com.jie.wealthmate.feature.menu.MenuScreenModel
import com.jie.wealthmate.feature.menu.categorySetting.addCategory.AddCategoryScreenModel
import com.jie.wealthmate.feature.menu.categorySetting.incomCategorySetting.IncomeCategorySettingScreenModel
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.CategoryRepositoryImpl
import org.koin.dsl.module

val commonModule = module {

    single<CategoryRepository> {
        CategoryRepositoryImpl(databaseDriverFactory = get())
    }

    // 여기에 ScreenModel, Repository 등 다른 공통 클래스들도 추가할 수 있습니다.
    factory { HomeScreenModel() }
    factory { CalendarScreenModel() }
    factory { AssetScreenModel() }
    factory { MenuScreenModel() }
    factory { AddCategoryScreenModel(get()) }
    factory { IncomeCategorySettingScreenModel(get()) }
}