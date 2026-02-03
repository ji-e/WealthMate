package com.jie.wealthmate.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.jie.wealthmate.database.eneity.CategoryConverters
import com.jie.wealthmate.DB_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSHomeDirectory

actual class DatabaseBuilder {
    actual fun build(): AppDatabase {
        val dbFile = NSHomeDirectory() + "/$DB_NAME"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile,
        )
            .addTypeConverter(CategoryConverters())
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}