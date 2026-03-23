package com.jie.wealthmate.di

import com.jie.wealthmate.account.AccountProvider
import com.jie.wealthmate.database.DatabaseSyncManager
import com.jie.wealthmate.feature.budget.BudgetViewModel
import com.jie.wealthmate.feature.budget.addBudget.AddBudgetViewModel
import com.jie.wealthmate.feature.budget.budgetDetail.BudgetDetailViewModel
import com.jie.wealthmate.feature.budget.budgetSetting.BudgetSettingViewModel
import com.jie.wealthmate.feature.budget.budgetYearDetail.BudgetYearDetailViewModel
import com.jie.wealthmate.feature.calendar.CalendarViewModel
import com.jie.wealthmate.feature.calendar.addHistory.AddHistoryViewModel
import com.jie.wealthmate.feature.calendar.historyDetail.HistoryDetailViewModel
import com.jie.wealthmate.feature.home.HomeViewModel
import com.jie.wealthmate.feature.home.categoryExpenses.CategoryExpensesViewModel
import com.jie.wealthmate.feature.home.paymentMethodExpenses.PaymentMethodExpensesViewModel
import com.jie.wealthmate.feature.home.preparednessStatus.PreparednessStatusViewModel
import com.jie.wealthmate.feature.main.MainViewModel
import com.jie.wealthmate.feature.menu.MenuViewModel
import com.jie.wealthmate.feature.menu.data.googleCloudShare.GoogleCloudShareViewModel
import com.jie.wealthmate.feature.menu.data.googleCloudSync.GoogleCloudSyncViewModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.CategoryManagementViewModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.editCategory.EditCategoryViewModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.modifyCategory.ModifyCategoryViewModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.PaymentMethodManagementViewModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.addPaymentMethod.AddPaymentMethodViewModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.modifyPaymentMethod.ModifyPaymentMethodViewModel
import com.jie.wealthmate.feature.menu.management.paymentMethodManagement.paymentMethodGroup.PaymentMethodGroupViewModel
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.RepeatHistoryManagementViewModel
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.addRepeatHistory.AddRepeatHistoryViewModel
import com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.repeatHistoryDetail.RepeatHistoryDetailViewModel
import com.jie.wealthmate.feature.search.SearchViewModel
import com.jie.wealthmate.network.HttpClientFactory
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.repository.AuthRepositoryImpl
import com.jie.wealthmate.repository.BudgetFirestoreRepositoryImpl
import com.jie.wealthmate.repository.BudgetRepository
import com.jie.wealthmate.repository.BudgetRepositoryDelegate
import com.jie.wealthmate.repository.BudgetRepositoryImpl
import com.jie.wealthmate.repository.CategoryFirestoreRepositoryImpl
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.CategoryRepositoryDelegate
import com.jie.wealthmate.repository.CategoryRepositoryImpl
import com.jie.wealthmate.repository.FirebaseRepository
import com.jie.wealthmate.repository.FirebaseRepositoryImpl
import com.jie.wealthmate.repository.GoogleRepository
import com.jie.wealthmate.repository.GoogleRepositoryImpl
import com.jie.wealthmate.repository.HistoryFirestoreRepositoryImpl
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.HistoryRepositoryDelegate
import com.jie.wealthmate.repository.HistoryRepositoryImpl
import com.jie.wealthmate.repository.InstallmentFirestoreRepositoryImpl
import com.jie.wealthmate.repository.InstallmentRepository
import com.jie.wealthmate.repository.InstallmentRepositoryDelegate
import com.jie.wealthmate.repository.InstallmentRepositoryImpl
import com.jie.wealthmate.repository.PaymentMethodFirestoreRepositoryImpl
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.repository.PaymentMethodRepositoryDelegate
import com.jie.wealthmate.repository.PaymentMethodRepositoryImpl
import com.jie.wealthmate.repository.RepeatCycleFirestoreRepositoryImpl
import com.jie.wealthmate.repository.RepeatCycleRepository
import com.jie.wealthmate.repository.RepeatCycleRepositoryDelegate
import com.jie.wealthmate.repository.RepeatCycleRepositoryImpl
import com.jie.wealthmate.usecase.ModifyHistoryUseCase
import com.jie.wealthmate.usecase.SaveHistoryUseCase
import com.jie.wealthmate.usecase.UpdateInstallmentUseCase
import com.jie.wealthmate.usecase.UpdateRepeatCycleUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val commonModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::AddHistoryViewModel)
    viewModelOf(::HistoryDetailViewModel)
    viewModelOf(::CategoryExpensesViewModel)
    viewModelOf(::PaymentMethodExpensesViewModel)
    viewModelOf(::PreparednessStatusViewModel)
    viewModelOf(::BudgetViewModel)
    viewModelOf(::BudgetSettingViewModel)
    viewModelOf(::AddBudgetViewModel)
    viewModelOf(::BudgetDetailViewModel)
    viewModelOf(::BudgetYearDetailViewModel)
    viewModelOf(::MenuViewModel)
    viewModelOf(::EditCategoryViewModel)
    viewModelOf(::ModifyCategoryViewModel)
    viewModelOf(::CategoryManagementViewModel)
    viewModelOf(::PaymentMethodManagementViewModel)
    viewModelOf(::PaymentMethodGroupViewModel)
    viewModelOf(::AddPaymentMethodViewModel)
    viewModelOf(::ModifyPaymentMethodViewModel)
    viewModelOf(::RepeatHistoryManagementViewModel)
    viewModelOf(::AddRepeatHistoryViewModel)
    viewModelOf(::RepeatHistoryDetailViewModel)
    viewModelOf(::GoogleCloudSyncViewModel)
    viewModelOf(::GoogleCloudShareViewModel)
    viewModelOf(::SearchViewModel)

    singleOf(::DatabaseSyncManager)
    single { HttpClientFactory(get()).create() }
    singleOf(::SaveHistoryUseCase)
    singleOf(::UpdateInstallmentUseCase)
    singleOf(::UpdateRepeatCycleUseCase)
    singleOf(::ModifyHistoryUseCase)

    single<FirebaseRepository> { FirebaseRepositoryImpl() }
    singleOf(::AccountProvider)

    // Category Repository
    singleOf(::CategoryRepositoryImpl)
    singleOf(::CategoryFirestoreRepositoryImpl)
    single<CategoryRepository> {
        CategoryRepositoryDelegate(
            accountProvider = get(),
            authRepository = get(),
            localRepository = get<CategoryRepositoryImpl>(),
            firestoreRepository = get<CategoryFirestoreRepositoryImpl>()
        )
    }

    // PaymentMethod Repository
    singleOf(::PaymentMethodRepositoryImpl)
    singleOf(::PaymentMethodFirestoreRepositoryImpl)
    single<PaymentMethodRepository> {
        PaymentMethodRepositoryDelegate(
            accountProvider = get(),
            localRepository = get<PaymentMethodRepositoryImpl>(),
            firestoreRepository = get<PaymentMethodFirestoreRepositoryImpl>()
        )
    }

    // History Repository
    singleOf(::HistoryRepositoryImpl)
    singleOf(::HistoryFirestoreRepositoryImpl)
    single<HistoryRepository> {
        HistoryRepositoryDelegate(
            accountProvider = get(),
            localRepository = get<HistoryRepositoryImpl>(),
            firestoreRepository = get<HistoryFirestoreRepositoryImpl>()
        )
    }

    // Installment Repository
    singleOf(::InstallmentRepositoryImpl)
    singleOf(::InstallmentFirestoreRepositoryImpl)
    single<InstallmentRepository> {
        InstallmentRepositoryDelegate(
            accountProvider = get(),
            localRepository = get<InstallmentRepositoryImpl>(),
            firestoreRepository = get<InstallmentFirestoreRepositoryImpl>()
        )
    }

    // RepeatCycle Repository
    singleOf(::RepeatCycleRepositoryImpl)
    singleOf(::RepeatCycleFirestoreRepositoryImpl)
    single<RepeatCycleRepository> {
        RepeatCycleRepositoryDelegate(
            accountProvider = get(),
            localRepository = get<RepeatCycleRepositoryImpl>(),
            firestoreRepository = get<RepeatCycleFirestoreRepositoryImpl>()
        )
    }

    // Budget Repository
    singleOf(::BudgetRepositoryImpl)
    singleOf(::BudgetFirestoreRepositoryImpl)
    single<BudgetRepository> {
        BudgetRepositoryDelegate(
            accountProvider = get(),
            localRepository = get<BudgetRepositoryImpl>(),
            firestoreRepository = get<BudgetFirestoreRepositoryImpl>()
        )
    }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<GoogleRepository> { GoogleRepositoryImpl(get(), get()) }
}
