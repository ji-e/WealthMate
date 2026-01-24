package com.jie.wealthmate.di

import com.jie.wealthmate.database.AppDatabase
import com.jie.wealthmate.database.DatabaseBuilder
import com.jie.wealthmate.database.dao.CategoryDao
import com.jie.wealthmate.database.eneity.CategoryConverters
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.CategoryRepositoryImpl
import org.koin.dsl.module

val databaseModule = module {
    single<CategoryConverters> { CategoryConverters() }
    single<AppDatabase> { get<DatabaseBuilder>().build() }
    single<CategoryDao> { get<AppDatabase>().categoryDao() }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
}