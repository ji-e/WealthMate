package com.jie.wealthmate.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.jie.wealthmate.database.eneity.CategoryConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSHomeDirectory

actual class DatabaseBuilder {
    actual fun build(): AppDatabase {
        val dbFile = NSHomeDirectory() + "/wm_database.db"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile,
        )
            .addTypeConverter(CategoryConverters())
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}