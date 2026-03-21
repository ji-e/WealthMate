package com.jie.wealthmate.di

import com.jie.wealthmate.database.AppDatabase
import com.jie.wealthmate.database.DatabaseProvider
import com.jie.wealthmate.database.dao.BudgetDao
import com.jie.wealthmate.database.dao.CategoryDao
import com.jie.wealthmate.database.dao.HistoryDao
import com.jie.wealthmate.database.dao.InstallmentDao
import com.jie.wealthmate.database.dao.PaymentMethodDao
import com.jie.wealthmate.database.dao.PaymentMethodGroupDao
import com.jie.wealthmate.database.dao.RepeatCycleDao
import com.jie.wealthmate.database.eneity.CategoryConverters
import org.koin.dsl.module

val databaseModule = module {
    single { DatabaseProvider(get()) }
    single<AppDatabase> { get<DatabaseProvider>().database }

    single<CategoryConverters> { CategoryConverters() }
    single<CategoryDao> { get<DatabaseProvider>().database.categoryDao() }

    single<PaymentMethodDao> { get<DatabaseProvider>().database.paymentMethodDao() }
    single<PaymentMethodGroupDao> { get<DatabaseProvider>().database.paymentMethodGroupDao() }

    single<HistoryDao> { get<DatabaseProvider>().database.historyDao() }

    single<InstallmentDao> { get<DatabaseProvider>().database.installmentDao() }

    single<RepeatCycleDao> { get<DatabaseProvider>().database.repeatCycleDao() }

    single<BudgetDao> { get<DatabaseProvider>().database.budgetDao() }

}
