package com.jie.wealthmate.di

import com.jie.wealthmate.database.DatabaseSyncManager
import com.jie.wealthmate.feature.asset.AssetScreenModel
import com.jie.wealthmate.feature.calendar.CalendarScreenModel
import com.jie.wealthmate.feature.calendar.addHistory.AddHistoryScreenModel
import com.jie.wealthmate.feature.calendar.historyDetail.HistoryDetailScreenModel
import com.jie.wealthmate.feature.home.HomeScreenModel
import com.jie.wealthmate.feature.menu.MenuScreenModel
import com.jie.wealthmate.feature.menu.data.googleCloudShare.GoogleCloudShareScreenModel
import com.jie.wealthmate.feature.menu.data.googleCloudSync.GoogleCloudSyncScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.CategoryManagementScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory.AddCategoryScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.modifyCategory.ModifyCategoryScreenModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.PaymentMethodManagementScreenModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.AddPaymentMethodScreenModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.modifyPaymentMethod.ModifyPaymentMethodScreenModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.paymentMethodGroup.PaymentMethodGroupScreenModel
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.RepeatHistoryManagementScreenModel
import com.jie.wealthmate.network.HttpClientFactory
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.repository.AuthRepositoryImpl
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.CategoryRepositoryImpl
import com.jie.wealthmate.repository.GoogleRepository
import com.jie.wealthmate.repository.GoogleRepositoryImpl
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.HistoryRepositoryImpl
import com.jie.wealthmate.repository.InstallmentRepository
import com.jie.wealthmate.repository.InstallmentRepositoryImpl
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.repository.PaymentMethodRepositoryImpl
import com.jie.wealthmate.repository.RepeatCycleRepository
import com.jie.wealthmate.repository.RepeatCycleRepositoryImpl
import com.jie.wealthmate.usecase.ModifyHistoryUseCase
import com.jie.wealthmate.usecase.SaveHistoryUseCase
import com.jie.wealthmate.usecase.UpdateInstallmentUseCase
import com.jie.wealthmate.usecase.UpdateRepeatCycleUseCase
import org.koin.dsl.module

val commonModule = module {
    single { DatabaseSyncManager(get(), get(), get()) }

    single { HttpClientFactory(get()).create() }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<GoogleRepository> { GoogleRepositoryImpl(get(), get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<PaymentMethodRepository> { PaymentMethodRepositoryImpl(get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get()) }
    single<InstallmentRepository> { InstallmentRepositoryImpl(get()) }
    single<RepeatCycleRepository> { RepeatCycleRepositoryImpl(get()) }


    single { SaveHistoryUseCase(get(), get(), get()) }
    single { UpdateInstallmentUseCase(get(), get()) }
    single { UpdateRepeatCycleUseCase(get(), get()) }
    single { ModifyHistoryUseCase(get(), get()) }


    // 홈
    factory { HomeScreenModel() }

    // 캘린더
    factory { CalendarScreenModel(get(), get()) }
    factory { AddHistoryScreenModel(get(), get(), get(), get()) }
    factory { HistoryDetailScreenModel(get(), get(), get(), get(), get(), get()) }


    // 자산
    factory { AssetScreenModel() }

    // 메뉴
    factory { MenuScreenModel(get(), get()) }
    factory { AddCategoryScreenModel(get()) }
    factory { ModifyCategoryScreenModel(get()) }
    factory { CategoryManagementScreenModel(get()) }
    factory { PaymentMethodManagementScreenModel(get()) }
    factory { PaymentMethodGroupScreenModel(get()) }
    factory { AddPaymentMethodScreenModel(get()) }
    factory { ModifyPaymentMethodScreenModel(get()) }
    factory { RepeatHistoryManagementScreenModel(get()) }

    factory { GoogleCloudSyncScreenModel(get(), get(), get()) }
    factory { GoogleCloudShareScreenModel(get(), get(), get()) }

}
