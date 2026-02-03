package com.jie.wealthmate.database

actual class DatabaseManager {
    actual fun getDatabaseBytes(databaseName: String): ByteArray? {
        TODO("Not yet implemented")
    }

    actual fun saveDatabaseBytes(bytes: ByteArray,databaseName: String,): Boolean {
        TODO("Not yet implemented")
    }

    actual fun closeDatabase() {
    }
}