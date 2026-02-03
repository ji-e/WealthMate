package com.jie.wealthmate.database

import android.content.Context

// androidMain
actual class DatabaseManager(
    private val context: Context,
    private val db: AppDatabase,
) {
    actual fun getDatabaseBytes(databaseName: String): ByteArray? {
        val dbFile = context.getDatabasePath(databaseName)
        return if (dbFile.exists()) {
            dbFile.readBytes()
        } else {
            null
        }
    }

    actual fun saveDatabaseBytes(bytes: ByteArray, databaseName: String): Boolean {
        val dbFile = context.getDatabasePath(databaseName)
        val walFile = context.getDatabasePath("$databaseName-wal")
        val shmFile = context.getDatabasePath("$databaseName-shm")
        println("실제 저장되는 절대 경로: ${dbFile.absolutePath}")
        return try {
            // 1. 임시 파일이 있다면 무조건 삭제 (가장 중요!)
            if (walFile.exists()) walFile.delete()
            if (shmFile.exists()) shmFile.delete()

            // 2. 메인 DB 파일 덮어쓰기
            dbFile.writeBytes(bytes)

            println("파일 저장 성공: ${dbFile.absolutePath}, 크기: ${bytes.size} bytes")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    actual fun closeDatabase() {
        if (db.isOpen) {
            db.close()
        }
    }
}

