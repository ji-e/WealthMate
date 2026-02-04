package com.jie.wealthmate.di

import com.jie.wealthmate.database.AppDatabase
import com.jie.wealthmate.database.DatabaseProvider
import com.jie.wealthmate.database.dao.CategoryDao
import com.jie.wealthmate.database.dao.HistoryDao
import com.jie.wealthmate.database.dao.InstallmentDao
import com.jie.wealthmate.database.dao.PaymentMethodDao
import com.jie.wealthmate.database.dao.PaymentMethodGroupDao
import com.jie.wealthmate.database.dao.RepeatCycleDao
import com.jie.wealthmate.database.eneity.CategoryConverters
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
import org.koin.dsl.module

val databaseModule = module {
    single { DatabaseProvider(get()) }
    single<AppDatabase> { get<DatabaseProvider>().database }

    single<CategoryConverters> { CategoryConverters() }
    single<CategoryDao> { get<DatabaseProvider>().database.categoryDao() }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }

    single<PaymentMethodDao> { get<DatabaseProvider>().database.paymentMethodDao() }
    single<PaymentMethodGroupDao> { get<DatabaseProvider>().database.paymentMethodGroupDao() }
    single<PaymentMethodRepository> { PaymentMethodRepositoryImpl(get()) }

    single<HistoryDao> { get<DatabaseProvider>().database.historyDao() }
    single<HistoryRepository> { HistoryRepositoryImpl(get()) }

    single<InstallmentDao> { get<DatabaseProvider>().database.installmentDao() }
    single<InstallmentRepository> { InstallmentRepositoryImpl(get()) }

    single<RepeatCycleDao> { get<DatabaseProvider>().database.repeatCycleDao() }
    single<RepeatCycleRepository> { RepeatCycleRepositoryImpl(get()) }

    single<GoogleRepository> { GoogleRepositoryImpl(get()) }
}
