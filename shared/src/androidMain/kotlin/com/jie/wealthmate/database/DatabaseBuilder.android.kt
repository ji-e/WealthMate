package com.jie.wealthmate.database

import android.content.Context
import androidx.room.Room
import com.jie.wealthmate.database.eneity.CategoryConverters

actual class DatabaseBuilder(private val context: Context) {
    actual fun build(): AppDatabase {
        val dbFile = context.getDatabasePath("wm_database.db")
        return Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath
        )
            .addTypeConverter(CategoryConverters())
            .build()
    }
}