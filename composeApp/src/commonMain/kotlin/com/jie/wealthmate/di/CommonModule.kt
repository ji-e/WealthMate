package com.jie.wealthmate.di

import com.jie.wealthmate.feature.asset.AssetScreenModel
import com.jie.wealthmate.feature.calendar.CalendarScreenModel
import com.jie.wealthmate.feature.calendar.addHistory.AddHistoryScreenModel
import com.jie.wealthmate.feature.home.HomeScreenModel
import com.jie.wealthmate.feature.menu.MenuScreenModel
import com.jie.wealthmate.feature.menu.categoryManagement.CategoryManagementScreenModel
import com.jie.wealthmate.feature.menu.categoryManagement.addCategory.AddCategoryScreenModel
import com.jie.wealthmate.feature.menu.categoryManagement.modifyCategory.ModifyCategoryScreenModel
import com.jie.wealthmate.feature.menu.paymentMethodManagement.PaymentMethodManagementScreenModel
import com.jie.wealthmate.feature.menu.paymentMethodManagement.addPaymentMethod.AddPaymentMethodScreenModel
import com.jie.wealthmate.feature.menu.paymentMethodManagement.modifyPaymentMethod.ModifyPaymentMethodScreenModel
import com.jie.wealthmate.feature.menu.paymentMethodManagement.paymentMethodGroup.PaymentMethodGroupScreenModel
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.CategoryRepositoryImpl
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.repository.PaymentMethodRepositoryImpl
import org.koin.dsl.module

val commonModule = module {

    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<PaymentMethodRepository> { PaymentMethodRepositoryImpl(get(), get()) }

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
    factory { CategoryManagementScreenModel(get()) }
    factory { PaymentMethodManagementScreenModel(get()) }
    factory { PaymentMethodGroupScreenModel(get()) }
    factory { AddPaymentMethodScreenModel(get()) }
    factory { ModifyPaymentMethodScreenModel(get()) }

}