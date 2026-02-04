package com.jie.wealthmate.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jie.wealthmate.database.eneity.CategoryConverters
import com.jie.wealthmate.DB_NAME

actual class DatabaseBuilder(private val context: Context) {
    actual fun build(): AppDatabase {
        val dbFile = context.getDatabasePath(DB_NAME)
        return Room.databaseBuilder<AppDatabase>(
            context = context,
            name = dbFile.absolutePath
        )
            .addTypeConverter(CategoryConverters())
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .build()
    }
}