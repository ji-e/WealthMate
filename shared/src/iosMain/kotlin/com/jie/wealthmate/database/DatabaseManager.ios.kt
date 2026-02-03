package com.jie.wealthmate.database
// iosMain
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.writeToFile
import platform.posix.memcpy

actual class DatabaseManager(private val db: AppDatabase) {
    actual fun getDatabaseBytes(databaseName: String): ByteArray? {
        val fileManager = NSFileManager.defaultManager
        // Application Support 디렉토리 경로 찾기
        val urls = fileManager.URLsForDirectory(NSApplicationSupportDirectory, NSUserDomainMask)
        val appSupportDir = urls.first() as? NSURL

        val dbUrl = appSupportDir?.URLByAppendingPathComponent(databaseName)
        val path = dbUrl?.path

        return if (path != null && fileManager.fileExistsAtPath(path)) {
            NSData.dataWithContentsOfFile(path)?.toKotlinByteArray()
        } else {
            null
        }
    }

    // DatabaseManager 클래스 내부에 추가
    @OptIn(ExperimentalForeignApi::class)
    actual fun saveDatabaseBytes(bytes: ByteArray, databaseName: String): Boolean {
        val fileManager = NSFileManager.defaultManager
        val urls = fileManager.URLsForDirectory(NSApplicationSupportDirectory, NSUserDomainMask)
        val appSupportDir = urls.first() as? NSURL ?: return false

        // 1. Application Support 디렉토리가 없으면 생성 (iOS 필수 절차)
        if (!fileManager.fileExistsAtPath(appSupportDir.path!!)) {
            fileManager.createDirectoryAtPath(
                appSupportDir.path!!,
                withIntermediateDirectories = true,
                attributes = null,
                error = null
            )
        }

        val dbUrl = appSupportDir.URLByAppendingPathComponent(databaseName) ?: return false
        val dbPath = dbUrl.path!!

        return try {
            // 2. 기존 파일이 있다면 삭제 (잠금 방지)
            if (fileManager.fileExistsAtPath(dbPath)) {
                fileManager.removeItemAtPath(dbPath, error = null)
            }

            // 3. 임시 파일(WAL, SHM)도 함께 삭제
            fileManager.removeItemAtPath("$dbPath-wal", error = null)
            fileManager.removeItemAtPath("$dbPath-shm", error = null)

            // 4. 데이터 쓰기
            val data = bytes.toNSData()
            data.writeToFile(dbPath, atomically = true)
        } catch (e: Exception) {
            println("iOS 저장 실패: ${e.message}")
            false
        }
    }

    actual fun closeDatabase() {
        db.close()
    }
}

// ByteArray를 NSData로 변환 (Unresolved reference 'toNSData' 해결)
@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toNSData(): NSData {
    if (this.isEmpty()) return NSData()
    return this.usePinned { pinned ->
        NSData.dataWithBytes(pinned.addressOf(0), this.size.toULong())
    }
}

// NSData를 ByteArray로 변환 (다운로드 시 필요)
@OptIn(ExperimentalForeignApi::class)
fun NSData.toKotlinByteArray(): ByteArray {
    val length = this.length.toInt()
    if (length == 0) return byteArrayOf()

    val bytes = ByteArray(length)
    bytes.usePinned { pinned ->
        memcpy(pinned.addressOf(0), this.bytes, this.length)
    }
    return bytes
}