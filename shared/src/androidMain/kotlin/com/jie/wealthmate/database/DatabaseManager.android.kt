package com.jie.wealthmate.database

import android.content.Context
import android.util.Log
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Android 플랫폼의 데이터베이스 관리 클래스입니다.
 * 공식 문서 참조: [Room Database](https://developer.android.com/training/data-storage/room)
 * [SQLite WAL Mode](https://www.sqlite.org/wal.html)
 */
actual class DatabaseManager(
    private val context: Context,
    private val database: AppDatabase,
) {
    companion object {
        private const val TAG = "DatabaseManager"
    }

    actual suspend fun closeDatabase() {
        try {
            if (database.isOpen) {
                performCheckpoint()
                database.close()
                Log.d(TAG, "Database closed successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to close database", e)
        }
    }

    /**
     * 데이터베이스 파일을 ByteArray로 변환하여 가져옵니다.
     * 백업 전에 WAL 데이터를 메인 DB 파일로 병합(Checkpoint)하는 과정이 중요합니다.
     */
    actual suspend fun getDatabaseBytes(databaseName: String): ByteArray? {
        return try {
            Log.d(TAG, "=== getDatabaseBytes 시작 ===")

            val dbFile = context.getDatabasePath(databaseName)

            // 1. 데이터베이스가 열려있다면 체크포인트 수행
            if (database.isOpen) {
                Log.d(TAG, "Performing TRUNCATE checkpoint before backup...")
                performCheckpoint()
                // 체크포인트가 파일 시스템에 반영되도록 잠시 대기
                Thread.sleep(100)
            }

            if (dbFile.exists().not()) {
                Log.e(TAG, "❌ 데이터베이스 파일이 존재하지 않습니다")
                return null
            }

            // 2. WAL 파일 확인 (데이터가 남아있는지 체크)
            val walFile = File(dbFile.parent, "$databaseName-wal")
            if (walFile.exists() && walFile.length() > 0) {
                Log.w(TAG, "⚠️ WAL 파일에 아직 데이터가 남아있습니다 (${walFile.length()} bytes)")
            }

            // 3. 파일 읽기
            val bytes = FileInputStream(dbFile).use { input ->
                input.readBytes()
            }

            Log.d(
                TAG,
                "✅ Successfully read ${bytes.size} bytes (Path: ${dbFile.absolutePath})"
            )
            Log.d(TAG, "=== getDatabaseBytes 완료 ===")
            bytes
        } catch (e: Exception) {
            Log.e(TAG, "❌ getDatabaseBytes 실패", e)
            null
        }
    }

    /**
     * ByteArray 데이터를 데이터베이스 파일로 저장합니다.
     */
    actual suspend fun saveDatabaseBytes(
        bytes: ByteArray,
        databaseName: String
    ): Boolean {
        var backupFile: File? = null
        var walBackupFile: File? = null
        var shmBackupFile: File? = null

        return try {
            Log.d(TAG, "=== saveDatabaseBytes 시작 (Size: ${bytes.size} bytes) ===")

            // 1. 기존 데이터베이스 닫기 (파일 교체 전 필수)
            if (database.isOpen) {
                performCheckpoint()
                database.close()
                Thread.sleep(150)
            }

            val dbFile = context.getDatabasePath(databaseName)
            val walFile = File(dbFile.parent, "$databaseName-wal")
            val shmFile = File(dbFile.parent, "$databaseName-shm")

            // 2. 기존 파일들 백업
            if (dbFile.exists()) {
                backupFile = File(dbFile.parent, "$databaseName.backup")
                dbFile.copyTo(
                    target = backupFile,
                    overwrite = true
                )
            }

            // 3. 기존 WAL/SHM 삭제 (새로운 DB 파일과 데이터 불일치 방지)
            if (walFile.exists()) {
                walBackupFile = File(walFile.parent, "$databaseName-wal.backup")
                walFile.copyTo(target = walBackupFile, overwrite = true)
                walFile.delete()
            }
            if (shmFile.exists()) {
                shmBackupFile = File(shmFile.parent, "$databaseName-shm.backup")
                shmFile.copyTo(target = shmBackupFile, overwrite = true)
                shmFile.delete()
            }

            // 4. 새로운 데이터베이스 파일 저장
            FileOutputStream(dbFile).use { output ->
                output.write(bytes)
                output.flush()
            }

            // 5. 무결성 검사
            val isValid = verifyDatabaseIntegrity(databaseName = databaseName)
            if (isValid.not()) {
                throw Exception("Restored database integrity check failed")
            }

            Log.d(TAG, "✅ Database integrity verified")

            // 6. 성공 시 백업 파일 삭제
            backupFile?.delete()
            walBackupFile?.delete()
            shmBackupFile?.delete()

            // 7. 데이터베이스 다시 열기
            Log.d(TAG, "Reopening database...")
            database.openHelper.writableDatabase

            Log.d(TAG, "✅ saveDatabaseBytes 성공")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ saveDatabaseBytes 실패", e)
            restoreFromBackup(
                databaseName = databaseName,
                backupFile = backupFile,
                walBackupFile = walBackupFile,
                shmBackupFile = shmBackupFile
            )
            false
        }
    }

    /**
     * PRAGMA wal_checkpoint(TRUNCATE)를 실행하여 WAL 데이터를 메인 DB로 병합합니다.
     */
    private fun performCheckpoint() {
        try {
            if (database.isOpen.not()) return

            val db: SupportSQLiteDatabase = database.openHelper.writableDatabase
            // TRUNCATE 모드는 WAL 파일의 내용을 DB로 모두 옮기고 WAL 파일 크기를 0으로 만듭니다.
            db.query("PRAGMA wal_checkpoint(TRUNCATE)").use { cursor ->
                if (cursor.moveToFirst()) {
                    val busy = cursor.getInt(0)
                    val log = cursor.getInt(1)
                    val checkpointed = cursor.getInt(2)
                    Log.d(TAG, "Checkpoint TRUNCATE - busy: $busy, log: $log, checkpointed: $checkpointed")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Checkpoint failed", e)
        }
    }

    private fun verifyDatabaseIntegrity(databaseName: String): Boolean {
        return try {
            val db: SupportSQLiteDatabase = database.openHelper.writableDatabase
            db.query("PRAGMA integrity_check").use { cursor ->
                if (cursor.moveToFirst()) {
                    val result = cursor.getString(0)
                    Log.d(TAG, "Integrity check result: $result")
                    result == "ok"
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Integrity check failed", e)
            false
        }
    }

    private fun restoreFromBackup(
        databaseName: String,
        backupFile: File?,
        walBackupFile: File?,
        shmBackupFile: File?
    ) {
        try {
            Log.w(TAG, "Restoring database from backup...")
            val dbFile = context.getDatabasePath(databaseName)
            backupFile?.copyTo(target = dbFile, overwrite = true)
            
            val walFile = File(dbFile.parent, "$databaseName-wal")
            walBackupFile?.copyTo(target = walFile, overwrite = true)
            
            val shmFile = File(dbFile.parent, "$databaseName-shm")
            shmBackupFile?.copyTo(target = shmFile, overwrite = true)
            
            database.openHelper.writableDatabase
            Log.d(TAG, "✅ Restored from backup successfully")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Critical: Failed to restore backup", e)
        }
    }
}
