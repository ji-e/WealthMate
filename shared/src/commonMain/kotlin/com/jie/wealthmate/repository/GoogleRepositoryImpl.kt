@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import GoogleDriveFileEntity
import com.jie.wealthmate.entity.GoogleAuthEntity
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.utils.io.core.toByteArray
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class GoogleRepositoryImpl(
    private val client: HttpClient,
    private val authRepository: AuthRepository
) : GoogleRepository {

    // 구글 토큰 엔드포인트에서 Access Token 교환 및 저장
    override suspend fun fetchGoogleAuth(authCode: String): GoogleAuthEntity? {
        try {
            val response: HttpResponse = client.post("https://oauth2.googleapis.com/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(Parameters.build {
                    append("grant_type", "authorization_code")
                    append("code", authCode)
                    append("client_id", CLIENT_ID)
                    append("client_secret", CLIENT_SECRET)
                    append("redirect_uri", "") // 필요한 경우 리다이렉트 URI 설정
                    append("access_type", "offline")
                    append("prompt", "consent")
                }))
            }

            return if (response.status.value == 200) {
                val authEntity = response.body<GoogleAuthEntity>()
                // 토큰 저장
                authRepository.saveAuthData(authEntity.accessToken, authEntity.refreshToken)
                authEntity
            } else {
                println("Fetch auth failed: ${response.bodyAsText()}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    override suspend fun uploadDatabase(
        dbBytes: ByteArray,
        fileName: String,
    ) {
        try {
            // 1. 기존 파일 검색
            val existingFileId = findFileByName(fileName)

            if (existingFileId != null) {
                // 2-1. 기존 파일이 있으면 업데이트
                updateFile(existingFileId, dbBytes)
            } else {
                // 2-2. 기존 파일이 없으면 새로 생성
                createFile(fileName, dbBytes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("데이터베이스 업로드 실패: ${e.message}")
        }
    }


    override suspend fun downloadDatabase(fileName: String): ByteArray {
        try {
            // 1. 파일 검색
            val fileId = findFileByName(fileName)
                ?: throw Exception("Google Drive에서 파일을 찾을 수 없습니다")

            // 2. 파일 다운로드
            return downloadFile(fileId)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("데이터베이스 다운로드 실패: ${e.message}")
        }
    }

    override suspend fun getFileList(): GoogleDriveFileEntity? {
        return try {
            val response: HttpResponse = client.get("https://www.googleapis.com/drive/v3/files") {
                parameter("spaces", "appDataFolder")
                parameter("fields", "files(id, name, createdTime, size)")
            }

            if (response.status.value == 200) {
                response.body<GoogleDriveFileEntity>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun createSharedFolder(folderName: String): String? {
        return try {
            val metadata = """
                {
                    "name": "$folderName",
                    "mimeType": "application/vnd.google-apps.folder"
                }
            """.trimIndent()

            val response: HttpResponse = client.post("https://www.googleapis.com/drive/v3/files") {
                contentType(ContentType.Application.Json)
                setBody(metadata)
            }

            if (response.status.value == 200) {
                val file = response.body<Map<String, String>>()
                file["id"]
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun grantPermission(fileId: String, email: String): Boolean {
        return try {
            val permissionRequest = """
                {
                    "role": "writer",
                    "type": "user",
                    "emailAddress": "$email"
                }
            """.trimIndent()

            val emailMessage = """
                가계부 공유 초대를 받았습니다. 
                앱의 '공유 연결' 화면에서 아래 초대 코드를 입력해 주세요:
                
                초대 코드: $fileId
            """.trimIndent()

            val response: HttpResponse = client.post("https://www.googleapis.com/drive/v3/files/$fileId/permissions") {
                parameter("sendNotificationEmail", true)
                parameter("emailMessage", emailMessage)
                contentType(ContentType.Application.Json)
                setBody(permissionRequest)
            }

            response.status.value == 200
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getOrCreateSharedFolder(folderName: String): String? {
        // 1. 로컬에 저장된 ID가 있는지 확인
        val savedId = authRepository.getSharedFolderId()
        if (savedId != null) {
            Napier.d("기존 공유 폴더 ID 사용 (로컬 저장소): $savedId")
            return savedId
        }

        // 2. 로컬에 없다면 구글 드라이브에서 동일한 이름의 폴더 검색 (앱 재설치 등의 경우 대비)
        val existingFolderId = findFolderByName(folderName)
        if (existingFolderId != null) {
            Napier.d("기존 공유 폴더 발견 (구글 드라이브 검색): $existingFolderId")
            authRepository.saveSharedFolderId(existingFolderId)
            return existingFolderId
        }

        // 3. 드라이브에도 없다면 새로 생성
        Napier.d("새로운 공유 폴더 생성 중: $folderName")
        val newFolderId = createSharedFolder(folderName)
        
        // 4. 생성된 ID를 로컬에 영구 저장
        if (newFolderId != null) {
            authRepository.saveSharedFolderId(newFolderId)
        }
        
        return newFolderId
    }

    override suspend fun connectToSharedFolder(folderId: String): GoogleDriveFileEntity? {
        return try {
            // 1. 연결 확인: 폴더 메타데이터 가져오기 (권한 체크 포함)
            val folderResponse: HttpResponse = client.get("https://www.googleapis.com/drive/v3/files/$folderId") {
                parameter("fields", "id, name, capabilities(canEdit)")
            }

            if (folderResponse.status.value != 200) {
                throw Exception("폴더를 찾을 수 없거나 접근 권한이 없습니다.")
            }

            // 2. 데이터 조회: 해당 폴더 내의 파일 목록 불러오기
            val filesResponse: HttpResponse = client.get("https://www.googleapis.com/drive/v3/files") {
                parameter("q", "'$folderId' in parents and trashed=false")
                parameter("fields", "files(id, name, createdTime, size)")
            }

            if (filesResponse.status.value == 200) {
                // 연결 성공 시 로컬에 폴더 ID 저장
                authRepository.saveSharedFolderId(folderId)
                filesResponse.body<GoogleDriveFileEntity>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * 폴더명으로 Google Drive 폴더 검색
     */
    private suspend fun findFolderByName(folderName: String): String? {
        return try {
            val response: HttpResponse = client.get("https://www.googleapis.com/drive/v3/files") {
                parameter("q", "name='$folderName' and mimeType='application/vnd.google-apps.folder' and trashed=false")
                parameter("fields", "files(id, name)")
            }

            if (response.status.value == 200) {
                val fileList = response.body<GoogleDriveFileEntity>()
                fileList.files.firstOrNull()?.id
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 파일명으로 Google Drive 파일 검색
     */
    private suspend fun findFileByName(fileName: String): String? {
        val response: HttpResponse = client.get("https://www.googleapis.com/drive/v3/files") {
            parameter("q", "name='$fileName' and trashed=false")
            parameter("spaces", "appDataFolder")
            parameter("fields", "files(id, name)")
        }

        return if (response.status.value == 200) {
            val fileList = response.body<GoogleDriveFileEntity>()
            fileList.files.firstOrNull()?.id
        } else {
            null
        }
    }

    /**
     * 새 파일 생성 (multipart upload)
     */
    private suspend fun createFile(
        fileName: String,
        fileBytes: ByteArray,
    ) {
        // 메타데이터 JSON
        val metadata = """
            {
                "name": "$fileName",
                "parents": ["appDataFolder"]
            }
        """.trimIndent()

        // Multipart 요청 생성
        val boundary = "boundary_${Clock.System.now().toEpochMilliseconds()}"
        val contentType = ContentType.parse("multipart/related; boundary=$boundary")

        val bodyPrefix = buildString {
            // Part 1: 메타데이터
            append("--$boundary\r\n")
            append("Content-Type: application/json; charset=UTF-8\r\n")
            append("\r\n")
            append(metadata)
            append("\r\n")

            // Part 2: 파일 데이터
            append("--$boundary\r\n")
            append("Content-Type: application/octet-stream\r\n")
            append("\r\n")
        }

        val bodyBytes = bodyPrefix.toByteArray() + fileBytes + "\r\n--$boundary--\r\n".toByteArray()

        client.post("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart") {
            headers {
                append(HttpHeaders.ContentType, contentType.toString())
            }
            setBody(bodyBytes)
        }
    }

    /**
     * 기존 파일 업데이트
     */
    private suspend fun updateFile(
        fileId: String,
        fileBytes: ByteArray,
    ) {
        client.patch("https://www.googleapis.com/upload/drive/v3/files/$fileId?uploadType=media") {
            contentType(ContentType.Application.OctetStream)
            setBody(fileBytes)
        }
    }

    /**
     * 파일 다운로드
     */
    private suspend fun downloadFile(fileId: String): ByteArray {
        val response: HttpResponse = client.get(
            "https://www.googleapis.com/drive/v3/files/$fileId?alt=media"
        )
        return response.readRawBytes()
    }

    companion object {
        const val CLIENT_ID = "1001016412934-av4h457eq1vtastir4hjdomf1bnd11hp.apps.googleusercontent.com"
        const val CLIENT_SECRET = "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"
    }
}
