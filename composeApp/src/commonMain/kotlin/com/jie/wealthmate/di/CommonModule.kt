package com.jie.wealthmate.di

import com.jie.wealthmate.database.DatabaseSyncManager
import com.jie.wealthmate.feature.budget.BudgetScreenModel
import com.jie.wealthmate.feature.budget.addBudget.AddBudgetScreenModel
import com.jie.wealthmate.feature.budget.budgetDetail.BudgetDetailScreenModel
import com.jie.wealthmate.feature.budget.budgetSetting.BudgetSettingScreenModel
import com.jie.wealthmate.feature.budget.budgetYearDetail.BudgetYearDetailScreenModel
import com.jie.wealthmate.feature.calendar.CalendarScreenModel
import com.jie.wealthmate.feature.calendar.addHistory.AddHistoryScreenModel
import com.jie.wealthmate.feature.calendar.historyDetail.HistoryDetailScreenModel
import com.jie.wealthmate.feature.home.HomeScreenModel
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.home.preparednessStatus.PreparednessStatusScreenModel
import com.jie.wealthmate.feature.menu.MenuScreenModel
import com.jie.wealthmate.feature.menu.data.googleCloudShare.GoogleCloudShareScreenModel
import com.jie.wealthmate.feature.menu.data.googleCloudSync.GoogleCloudSyncScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.CategoryManagementScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory.AddCategoryScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.modifyCategory.ModifyCategoryScreenModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.PaymentMethodManagementScreenModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.AddPaymentMethodScreenModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.modifyPaymentMethod.ModifyPaymentMethodScreenModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.paymentMethodGroup.PaymentMethodGroupScreenModel
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.RepeatHistoryManagementScreenModel
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.AddRepeatHistoryScreenModel
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.repeatHistoryDetail.RepeatHistoryDetailScreenModel
import com.jie.wealthmate.feature.search.SearchScreenModel
import com.jie.wealthmate.network.HttpClientFactory
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.repository.AuthRepositoryImpl
import com.jie.wealthmate.repository.BudgetRepository
import com.jie.wealthmate.repository.BudgetRepositoryImpl
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
import kotlinx.datetime.LocalDate
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
    single<BudgetRepository> { BudgetRepositoryImpl(get()) }


    single { SaveHistoryUseCase(get(), get(), get()) }
    single { UpdateInstallmentUseCase(get(), get()) }
    single { UpdateRepeatCycleUseCase(get(), get()) }
    single { ModifyHistoryUseCase(get(), get()) }


    // 홈
    factory { HomeScreenModel(get(), get()) }
    factory { (initialStatusType: StatusType, initialLargeCategory: LargeCategoryEnum) ->
        PreparednessStatusScreenModel(get(), initialStatusType, initialLargeCategory)
    }

    // 캘린더
    factory { CalendarScreenModel(get(), get()) }
    factory { AddHistoryScreenModel(get(), get(), get(), get()) }
    factory { HistoryDetailScreenModel(get(), get(), get(), get(), get(), get()) }


    // 예산
    factory { BudgetScreenModel(get(), get()) }
    factory { BudgetSettingScreenModel(get(), get()) }
    factory { AddBudgetScreenModel(get(), get()) }
    factory { (selectedMonth: LocalDate) ->
        BudgetDetailScreenModel(get(), get(), get(), selectedMonth)
    }
    factory { (selectedYear: String) ->
        BudgetYearDetailScreenModel(selectedYear, get(), get(), get())
    }


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
    factory { AddRepeatHistoryScreenModel(get(), get(), get()) }
    factory { RepeatHistoryDetailScreenModel(get(), get(), get()) }

    factory { GoogleCloudSyncScreenModel(get(), get(), get()) }
    factory { GoogleCloudShareScreenModel(get(), get(), get()) }

    // 검색
    factory { SearchScreenModel(get(), get(), get()) }

}
