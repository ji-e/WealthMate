package com.jie.wealthmate.database

import com.jie.wealthmate.DB_NAME

expect class DatabaseManager {
    fun closeDatabase() // 추가
    fun getDatabaseBytes(databaseName: String = DB_NAME): ByteArray?
    fun saveDatabaseBytes(bytes: ByteArray, databaseName: String = DB_NAME): Boolean
}

