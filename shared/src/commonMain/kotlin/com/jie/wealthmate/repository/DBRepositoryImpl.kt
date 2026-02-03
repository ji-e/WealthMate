@file:OptIn(ExperimentalTime::class)

package com.jie.wealthmate.repository

import com.jie.wealthmate.BACK_UP_DB_NAME
import com.jie.wealthmate.database.DatabaseManager
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime

@Serializable
data class GoogleDriveFileResponse(
    val files: List<DriveFile>,
)

@Serializable
data class DriveFile(
    val id: String,
    val name: String,
    val modifiedTime: String? = null,
)

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
)

class DBRepositoryImpl(
    private val dbManager: DatabaseManager,
) : DBRepository {

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
            val tokenData = jsonHelper.decodeFromString<TokenResponse>(responseBody)
            val realToken = tokenData.accessToken
            println("Token Response: $responseBody")
            println("realToken: $realToken")
            return realToken // 여기서 access_token만 추출
        } catch (e: Exception) {
            return null
        }
    }


    override suspend fun syncDatabaseToDrive(
        accessToken: String,
        dbBytes: ByteArray,
        fileName: String,
    ) {
        val client = HttpClient()
        val json = Json { ignoreUnknownKeys = true }

        try {
            // 1. 기존에 업로드된 파일이 있는지 검색 (파일명으로 ID 찾기)
            val searchResponse = client.get("https://www.googleapis.com/drive/v3/files") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
                parameter("q", "name = '$fileName'")
                parameter("spaces", "appDataFolder")
            }

            val fileList =
                json.decodeFromString<GoogleDriveFileResponse>(searchResponse.bodyAsText())
            val existingFileId = fileList.files.firstOrNull()?.id

            if (existingFileId != null) {
                // 2. 파일이 존재하면 업데이트 (PATCH)
                val updateResponse = client.patch("https://www.googleapis.com/upload/drive/v3/files/$existingFileId") {
                    header(HttpHeaders.Authorization, "Bearer $accessToken")
                    parameter("uploadType", "media")

                    // 이 두 줄이 데이터 파손을 막는 핵심입니다!
                    contentType(ContentType.Application.OctetStream)
                    setBody(dbBytes)
                }
                if (updateResponse.status.isSuccess()) {
                    println("기존 백업 업데이트 완료! 전송 크기: ${dbBytes.size}")
                }
            } else {
                // 3. 파일이 없으면 신규 생성 (Multipart POST)
                // 간단하게 하기 위해 우선 media 업로드 후 이름을 설정하는 방식을 쓰거나, 아래처럼 업로드합니다.
                // 수정된 syncDatabaseToDrive 내부의 업로드 부분
                val createResponse = client.post("https://www.googleapis.com/upload/drive/v3/files") {
                    header(HttpHeaders.Authorization, "Bearer $accessToken")
                    parameter("uploadType", "multipart")

                    // ⚠️ 중요: MultiPartFormDataContent를 직접 쓰지 않고 아래 방식을 권장합니다.
                    // 구글 API는 각 파트의 이름을 'metadata'와 'file'로 명시하는 것보다
                    // 순서와 타입을 더 중요하게 봅니다.
                    setBody(MultiPartFormDataContent(
                        formData {
                            // 파트 1: 메타데이터 (반드시 첫 번째여야 함)
                            append("metadata", "{\"name\": \"$fileName\", \"parents\": [\"appDataFolder\"]}",
                                Headers.build {
                                    append(HttpHeaders.ContentType, "application/json; charset=UTF-8")
                                }
                            )
                            // 파트 2: 실제 DB 파일
                            append("file", dbBytes, Headers.build {
                                append(HttpHeaders.ContentType, "application/octet-stream")
                                // filename이 포함되어야 구글 서버가 파일로 정확히 인식합니다.
                                append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"$fileName\"")
                            })
                        }
                    ))
                }

                val responseText = createResponse.bodyAsText()
                println("구글 응답 코드: ${createResponse.status}")
                println("구글 응답 내용: $responseText")

                if (createResponse.status.isSuccess()) {
                    println("새로운 백업 생성 완료!")
                } else {
                    // 실패 시 에러 메시지를 분석해야 합니다 (예: 403 - 권한부족, 400 - 형식오류)
                    println("백업 생성 실패: ${createResponse.status}")
                }
            }
        } catch (e: Exception) {
            println("동기화 중 에러 발생: ${e.message}")
        }
    }

    // 바이트를 깨뜨리지 않는 전송 방식
    fun buildSafeMultipartBody(boundary: String, fileName: String, bytes: ByteArray): ByteArray {
        val jsonPart = "--$boundary\r\n" +
                "Content-Type: application/json; charset=UTF-8\r\n\r\n" +
                "{\"name\": \"$fileName\", \"parents\": [\"appDataFolder\"]}\r\n" +
                "--$boundary\r\n" +
                "Content-Type: application/octet-stream\r\n\r\n"

        val endPart = "\r\n--$boundary--"

        // 문자열 + 바이트 + 문자열을 합쳐서 하나의 큰 ByteArray로 만듦
        return jsonPart.encodeToByteArray() + bytes + endPart.encodeToByteArray()
    }

    override suspend fun checkAndDownloadBackup(accessToken: String) {
        val client = HttpClient()
        val json = Json { ignoreUnknownKeys = true }

        try {
            // 1. 드라이브에 파일이 있는지 확인
            val response = client.get("https://www.googleapis.com/drive/v3/files") {
                header("Authorization", "Bearer ${accessToken.trim()}")
                parameter("spaces", "appDataFolder")
                parameter("fields", "files(id, name, modifiedTime)") // 수정 시간 포함
            }

            val fileList = json.decodeFromString<GoogleDriveFileResponse>(response.bodyAsText())
            val remoteFile = fileList.files.firstOrNull { it.name == BACK_UP_DB_NAME }

            if (remoteFile != null) {
                println("백업 파일 발견! ID: ${remoteFile.id}, 수정시간: ${remoteFile.modifiedTime}")

                // 2. 실제 파일 데이터 다운로드
                val downloadResponse =
                    client.get("https://www.googleapis.com/drive/v3/files/${remoteFile.id}") {
                        header("Authorization", "Bearer ${accessToken.trim()}")
                        parameter("alt", "media")
                    }

                val downloadedBytes = downloadResponse.readRawBytes()
                println("downloadedBytes::: ${downloadedBytes.size}")

                // 3. 기기에 저장 (덮어쓰기)
                // 주의: 호출 전 Room DB 인스턴스를 close() 해야 안전합니다.
                dbManager.closeDatabase()
                val success = dbManager.saveDatabaseBytes(downloadedBytes)

                if (success) {
                    println("동기화 성공! 앱을 재시작하거나 데이터를 새로고침하세요.")
                } else {
                    println("동기화 실패!")
                }
            } else {
                println("드라이브에 백업 파일이 없습니다.")
            }
        } catch (e: Exception) {
            println("다운로드 중 에러: ${e.message}")
        }
    }
}

