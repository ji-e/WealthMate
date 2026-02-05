package com.jie.wealthmate.network

import com.jie.wealthmate.entity.GoogleAuthEntity
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.repository.GoogleRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private val authRepository: AuthRepository
) {
    fun create(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = authRepository.getAccessToken()
                        val refreshToken = authRepository.getRefreshToken()
                        if (accessToken != null) {
                            BearerTokens(accessToken, refreshToken ?: "")
                        } else {
                            null
                        }
                    }

                    refreshTokens {
                        val refreshToken = authRepository.getRefreshToken() ?: return@refreshTokens null

                        try {
                            // 토큰 갱신 요청을 위한 별도의 클라이언트 (무한 루프 방지)
                            val refreshClient = HttpClient {
                                install(ContentNegotiation) {
                                    json(Json { ignoreUnknownKeys = true })
                                }
                            }

                            val response = refreshClient.post("https://oauth2.googleapis.com/token") {
                                contentType(ContentType.Application.FormUrlEncoded)
                                setBody(FormDataContent(Parameters.build {
                                    append("grant_type", "refresh_token")
                                    append("refresh_token", refreshToken)
                                    append("client_id", GoogleRepositoryImpl.CLIENT_ID)
                                    append("client_secret", GoogleRepositoryImpl.CLIENT_SECRET)
                                }))
                            }

                            if (response.status.value == 200) {
                                val newAuth = response.body<GoogleAuthEntity>()
                                authRepository.saveAuthData(
                                    accessToken = newAuth.accessToken,
                                    refreshToken = newAuth.refreshToken ?: refreshToken,
                                )
                                BearerTokens(newAuth.accessToken, newAuth.refreshToken ?: refreshToken)
                            } else {
                                authRepository.clearAuthData()
                                null
                            }
                        } catch (e: Exception) {
                            authRepository.clearAuthData()
                            null
                        }
                    }

                    // Google Drive API 등에만 Bearer 토큰 적용 (필요시 조건 추가)
                    sendWithoutRequest { request ->
                        request.url.host.contains("googleapis.com")
                    }
                }
            }
        }
    }
}
