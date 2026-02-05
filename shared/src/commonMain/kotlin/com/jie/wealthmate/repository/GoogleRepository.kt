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

    // 호스트용: 공유 폴더를 가져오거나 없으면 생성
    suspend fun getOrCreateSharedFolder(folderName: String): String?

    // 3단계: 게스트용 - 공유 폴더 연결 확인 및 파일 목록 가져오기
    suspend fun connectToSharedFolder(folderId: String): GoogleDriveFileEntity?

    // 4단계: 공유 폴더 전용 기능
    suspend fun uploadToSharedFolder(folderId: String, fileName: String, dbBytes: ByteArray)
    suspend fun getFilesFromSharedFolder(folderId: String): GoogleDriveFileEntity?
    suspend fun downloadFileById(fileId: String): ByteArray
}
