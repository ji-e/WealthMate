package com.jie.wealthmate.repository

import com.jie.wealthmate.BACK_UP_DB_NAME
import com.jie.wealthmate.entity.GoogleAuthEntity
import GoogleDriveFileEntity


interface GoogleRepository {
    suspend fun fetchGoogleAuth(authCode: String): GoogleAuthEntity?

    suspend fun uploadDatabase(
        dbBytes: ByteArray,
        fileName: String = BACK_UP_DB_NAME,
    )

    suspend fun downloadDatabase(
        fileName: String = BACK_UP_DB_NAME,
    ): ByteArray

    suspend fun getFileList(): GoogleDriveFileEntity?

    // 1단계: 공유 폴더 생성
    suspend fun createSharedFolder(folderName: String): String?

    // 1단계: 상대방에게 권한 부여
    suspend fun grantPermission(fileId: String, email: String): Boolean
    suspend fun getOrCreateSharedFolder(folderName: String): String?
}
