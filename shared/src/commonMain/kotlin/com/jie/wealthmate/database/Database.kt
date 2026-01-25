package com.jie.wealthmate.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.jie.wealthmate.database.dao.CategoryDao
import com.jie.wealthmate.database.dao.PaymentMethodDao
import com.jie.wealthmate.database.dao.PaymentMethodGroupDao
import com.jie.wealthmate.database.eneity.CategoryConverters
import com.jie.wealthmate.database.eneity.CategoryEntity
import com.jie.wealthmate.database.eneity.PaymentMethodEntity
import com.jie.wealthmate.database.eneity.PaymentMethodGroupEntity

@Database(
    entities = [
        CategoryEntity::class,
        PaymentMethodEntity::class,
        PaymentMethodGroupEntity::class,
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3)
    ]
)

@TypeConverters(CategoryConverters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun paymentMethodGroupDao(): PaymentMethodGroupDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}