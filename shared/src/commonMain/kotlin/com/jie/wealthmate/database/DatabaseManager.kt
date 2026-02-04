package com.jie.wealthmate.database

import com.jie.wealthmate.DB_NAME

expect class DatabaseManager {
    suspend fun closeDatabase()
    suspend fun getDatabaseBytes(databaseName: String = DB_NAME): ByteArray?
    suspend fun saveDatabaseBytes(bytes: ByteArray, databaseName: String = DB_NAME): Boolean
}

