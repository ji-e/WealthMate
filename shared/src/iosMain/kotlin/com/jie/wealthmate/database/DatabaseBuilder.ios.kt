package com.jie.wealthmate.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.jie.wealthmate.DB_NAME
import com.jie.wealthmate.database.eneity.CategoryConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual class DatabaseBuilder {
    actual fun build(): AppDatabase {
        val dbFile = (NSFileManager.defaultManager.URLsForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask
        ).first() as NSURL).URLByAppendingPathComponent(DB_NAME)!!.path!!

        return Room.databaseBuilder<AppDatabase>(
            name = dbFile,
        )
            .addTypeConverter(CategoryConverters())
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
