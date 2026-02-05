package com.jie.wealthmate.network

import com.jie.wealthmate.entity.GoogleAuthEntity
import com.jie.wealthmate.repository.AuthRepository
import com.jie.wealthmate.repository.GoogleRepositoryImpl
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private val authRepository: AuthRepository,
) {
    fun create(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d("Ktor: $message")
                    }
                }
                level = LogLevel.INFO
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = authRepository.getAccessToken()
                        val refreshToken = authRepository.getRefreshToken()

                        Napier.d(
                            "Loading tokens: access=${accessToken?.take(10)}..., " +
                                    "refresh=${refreshToken?.take(10)}..."
                        )
                        if (accessToken != null) {
                            BearerTokens(accessToken, refreshToken ?: "")
                        } else {
                            null
                        }
                    }

                    refreshTokens {
                        val refreshToken = authRepository.getRefreshToken()
                        if (refreshToken == null) {
                            Napier.e("Refresh token is null, cannot refresh")
                            return@refreshTokens null
                        }

                        Napier.d("Attempting to refresh token...")
                        try {
                            val refreshClient = HttpClient {
                                install(ContentNegotiation) {
                                    json(Json { ignoreUnknownKeys = true })
                                }
                            }

                            val response =
                                refreshClient.post("https://oauth2.googleapis.com/token") {
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
                                Napier.d("Token refresh successful")

                                val newAccessToken = newAuth.accessToken
                                val newRefreshToken = newAuth.refreshToken ?: refreshToken

                                authRepository.saveAuthData(
                                    accessToken = newAccessToken,
                                    refreshToken = newRefreshToken,
                                )
                                BearerTokens(newAccessToken, newRefreshToken)
                            } else {
                                val errorBody = response.bodyAsText()
                                Napier.e("Token refresh failed with status ${response.status}: $errorBody")
                                // 여기서 바로 clearAuthData를 호출하면 무한 루프나 원치 않는 로그아웃이 발생할 수 있으므로 신중해야 합니다.
                                // 400이나 401 에러인 경우 리프레시 토큰 자체가 무효화된 것일 수 있습니다.
                                if (response.status.value in 400..401) {
                                    authRepository.clearAuthData()
                                }
                                null
                            }
                        } catch (e: Exception) {
                            Napier.e("Exception during token refresh", e)
                            null
                        }
                    }

                    sendWithoutRequest { request ->
                        // googleapis.com 호스트를 포함하되, 토큰 갱신 엔드포인트는 제외 (무한 루프 및 인증 오류 방지)
                        val isGoogleApi = request.url.host.contains("googleapis.com")
                        val isTokenEndpoint = request.url.encodedPath.contains("/token")
                        isGoogleApi && !isTokenEndpoint
                    }
                }
            }
        }
    }
}
