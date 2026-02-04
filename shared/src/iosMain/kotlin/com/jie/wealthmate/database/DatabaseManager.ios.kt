package com.jie.wealthmate.database

import androidx.room.useReaderConnection
import androidx.room.execSQL
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.getBytes
import platform.Foundation.writeToFile
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

/**
 * iOS 플랫폼용 데이터베이스 관리자입니다.
 * 최신 안드로이드 권장 사항에 따라 작성되었습니다.
 * 참조: [SQLite WAL Mode](https://www.sqlite.org/wal.html)
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual class DatabaseManager(
    private val database: AppDatabase,
) {
    private val tag = "DatabaseManager"

    /**
     * 데이터베이스를 안전하게 닫고 WAL 데이터를 병합합니다.
     */
    actual suspend fun closeDatabase() {
        withContext(context = NonCancellable) {
            try {
                performCheckpoint()
                database.close()
                println("[$tag] 데이터베이스 닫기 완료")
            } catch (e: Exception) {
                println("[$tag] 데이터베이스 닫기 실패: ${e.message}")
            }
        }
    }

    /**
     * WAL 파일을 메인 DB 파일로 강제 병합합니다.
     * TRUNCATE 모드는 WAL 파일을 0으로 만들어 데이터 누락을 방지합니다.
     */
    private suspend fun performCheckpoint() {
        try {
            // (추측) 코루틴 취소로 인한 중단을 방지하기 위해 사용
            database.useReaderConnection { connection ->
                connection.execSQL(sql = "PRAGMA wal_checkpoint(TRUNCATE)")
            }
            println("[$tag] 체크포인트(TRUNCATE) 성공")
        } catch (e: Exception) {
            println("[$tag] 체크포인트 수행 중 알림: ${e.message}")
        }
    }

    actual suspend fun getDatabaseBytes(
        databaseName: String
    ): ByteArray? {
        return try {
            // 1. 백업 전 최신 데이터 병합
            performCheckpoint()

            val dbPath = getDatabasePath(databaseName = databaseName) ?: return null
            val fileManager = NSFileManager.defaultManager

            if (fileManager.fileExistsAtPath(path = dbPath).not()) {
                println("[$tag] 파일을 찾을 수 없음: $dbPath")
                return null
            }

            // 2. 파일 데이터를 ByteArray로 변환
            val data = NSData.dataWithContentsOfFile(path = dbPath) ?: return null

            // (추측) 61440 바이트는 초기화된 빈 DB 크기입니다.
            if (data.length.toLong() <= 61440L) {
                println("[$tag] 경고: 추출된 데이터 크기가 매우 작습니다 (${data.length} bytes)")
            }

            val byteArray = ByteArray(size = data.length.toInt())
            byteArray.usePinned { pinned ->
                data.getBytes(
                    buffer = pinned.addressOf(index = 0),
                    length = data.length
                )
            }

            println("[$tag] ${byteArray.size} 바이트 추출 성공")
            byteArray
        } catch (e: Exception) {
            println("[$tag] getDatabaseBytes 실패: ${e.message}")
            null
        }
    }

    actual suspend fun saveDatabaseBytes(
        bytes: ByteArray,
        databaseName: String
    ): Boolean {
        return withContext(context = NonCancellable) {
            try {
                val dbPath = getDatabasePath(databaseName = databaseName) ?: return@withContext false
                val fileManager = NSFileManager.defaultManager

                // 1. 기존 DB 정리
                performCheckpoint()
                database.close()

                // 2. 새 데이터 기록
                val data = bytes.usePinned { pinned ->
                    NSData.create(
                        bytes = pinned.addressOf(index = 0),
                        length = bytes.size.toULong()
                    )
                }

                val success = data.writeToFile(
                    path = dbPath,
                    atomically = true
                )

                if (success.not()) throw Exception("파일 기록 실패")

                // 3. 이전 세션의 WAL/SHM 파일 제거
                fileManager.removeItemAtPath(path = "$dbPath-wal", error = null)
                fileManager.removeItemAtPath(path = "$dbPath-shm", error = null)

                true
            } catch (e: Exception) {
                println("[$tag] saveDatabaseBytes 실패: ${e.message}")
                false
            }
        }
    }

    private fun getDatabasePath(
        databaseName: String
    ): String? {
        val fileManager = NSFileManager.defaultManager
        val documentsDirectory = fileManager.URLsForDirectory(
            directory = NSDocumentDirectory,
            inDomains = NSUserDomainMask
        ).firstOrNull() as? NSURL

        return documentsDirectory
            ?.URLByAppendingPathComponent(pathComponent = databaseName)
            ?.path
    }
}