package com.jie.wealthmate.di

import com.jie.wealthmate.database.AppDatabase
import com.jie.wealthmate.database.DatabaseBuilder
import com.jie.wealthmate.database.dao.CategoryDao
import com.jie.wealthmate.database.dao.HistoryDao
import com.jie.wealthmate.database.dao.PaymentMethodDao
import com.jie.wealthmate.database.dao.PaymentMethodGroupDao
import com.jie.wealthmate.database.dao.RepeatCycleDao
import com.jie.wealthmate.database.eneity.CategoryConverters
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.CategoryRepositoryImpl
import com.jie.wealthmate.repository.HistoryRepository
import com.jie.wealthmate.repository.HistoryRepositoryImpl
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.repository.PaymentMethodRepositoryImpl
import com.jie.wealthmate.repository.RepeatCycleRepository
import com.jie.wealthmate.repository.RepeatCycleRepositoryImpl
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> { get<DatabaseBuilder>().build() }

    single<CategoryConverters> { CategoryConverters() }
    single<CategoryDao> { get<AppDatabase>().categoryDao() }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }

    single<PaymentMethodDao> { get<AppDatabase>().paymentMethodDao() }
    single<PaymentMethodGroupDao> { get<AppDatabase>().paymentMethodGroupDao() }
    single<PaymentMethodRepository> { PaymentMethodRepositoryImpl(get(), get()) }

    single<HistoryDao> { get<AppDatabase>().historyDao() }
    single<HistoryRepository> { HistoryRepositoryImpl(get()) }

    single<RepeatCycleDao> { get<AppDatabase>().repeatCycleDao() }
    single<RepeatCycleRepository> { RepeatCycleRepositoryImpl(get()) }

}
