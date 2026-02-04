@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import GoogleDriveFileEntity
import com.jie.wealthmate.database.DatabaseManager
import com.jie.wealthmate.entity.GoogleAuthEntity
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlin.time.ExperimentalTime
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Clock


class GoogleRepositoryImpl(
    private val dbManager: DatabaseManager,
) : GoogleRepository {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    this.ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
    }


    // 구글 토큰 엔드포인트에서 Access Token 교환
    override suspend fun fetchAccessToken(authCode: String): String? {
        val client = HttpClient() // 특정 엔진(OkHttp 등)을 지정하거나 기본 생성자 사용

        try {
            val response: HttpResponse = client.post("https://oauth2.googleapis.com/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(Parameters.build {
                    append("grant_type", "authorization_code")
                    append("code", authCode)
                    // 구글 콘솔 '웹 애플리케이션'의 ID와 Secret
                    append(
                        "client_id",
                        "1001016412934-av4h457eq1vtastir4hjdomf1bnd11hp.apps.googleusercontent.com"
                    )
                    append("client_secret", "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") // 콘솔에서 복사
                    append("redirect_uri", "")
                }))
            }

            // 결과에서 access_token 파싱 (JSON 역직렬화 필요)
            val responseBody = response.bodyAsText()
            val jsonHelper = Json { ignoreUnknownKeys = true }
            val googleAuthData = jsonHelper.decodeFromString<GoogleAuthEntity>(responseBody)
            val accessToken = googleAuthData.accessToken

            println("GoogleAuthEntity::: $GoogleAuthEntity")

            return accessToken
        } catch (e: Exception) {
            return null
        }
    }


    override suspend fun uploadDatabase(
        accessToken: String,
        dbBytes: ByteArray,
        fileName: String,
    ) {
        try {
            // 1. 기존 파일 검색
            val existingFileId = findFileByName(accessToken, fileName)

            if (existingFileId != null) {
                // 2-1. 기존 파일이 있으면 업데이트
                updateFile(accessToken, existingFileId, dbBytes)
            } else {
                // 2-2. 기존 파일이 없으면 새로 생성
                createFile(accessToken, fileName, dbBytes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("데이터베이스 업로드 실패: ${e.message}")
        }
    }


    override suspend fun downloadDatabase(accessToken: String, fileName: String): ByteArray {
        try {
            // 1. 파일 검색
            val fileId = findFileByName(accessToken, fileName)
                ?: throw Exception("Google Drive에서 파일을 찾을 수 없습니다")

            // 2. 파일 다운로드
            return downloadFile(accessToken, fileId)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("데이터베이스 다운로드 실패: ${e.message}")
        }
    }

    /**
     * 파일명으로 Google Drive 파일 검색
     */
    private suspend fun findFileByName(accessToken: String, fileName: String): String? {
        val response: HttpResponse = client.get("https://www.googleapis.com/drive/v3/files") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            parameter("q", "name='$fileName' and trashed=false")
            parameter("spaces", "appDataFolder")
            parameter("fields", "files(id, name)")
        }

        val fileList = Json.decodeFromString<GoogleDriveFileEntity>(response.bodyAsText())
        return fileList.files.firstOrNull()?.id
    }

    /**
     * 새 파일 생성 (multipart upload)
     */
    private suspend fun createFile(
        accessToken: String,
        fileName: String,
        fileBytes: ByteArray
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

        val body = buildString {
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

        val bodyBytes = body.toByteArray() + fileBytes + "\r\n--$boundary--\r\n".toByteArray()

        client.post("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
                append(HttpHeaders.ContentType, contentType.toString())
            }
            setBody(bodyBytes)
        }
    }

    /**
     * 기존 파일 업데이트
     */
    private suspend fun updateFile(
        accessToken: String,
        fileId: String,
        fileBytes: ByteArray
    ) {
        client.patch("https://www.googleapis.com/upload/drive/v3/files/$fileId?uploadType=media") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
                append(HttpHeaders.ContentType, "application/octet-stream")
            }
            setBody(fileBytes)
        }
    }

    /**
     * 파일 다운로드
     */
    private suspend fun downloadFile(accessToken: String, fileId: String): ByteArray {
        val response: HttpResponse = client.get(
            "https://www.googleapis.com/drive/v3/files/$fileId?alt=media"
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }

        return response.readBytes()
    }

    fun close() {
        client.close()
    }
}

