package com.jie.wealthmate.database

import app.cash.sqldelight.db.SqlDriver

const val DATABASE_NAME = "wealthmate.db"

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
