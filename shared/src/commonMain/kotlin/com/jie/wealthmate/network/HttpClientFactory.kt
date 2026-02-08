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
import kotlinx.coroutines.IO
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
                        
                        Napier.d("Attempting to refresh token...")
                        try {
                            val refreshClient = HttpClient {
                                install(ContentNegotiation) {
                                    json(Json { ignoreUnknownKeys = true })
                                }
                            }

                            // 1. 리프레시 토큰으로 먼저 시도
                            if (refreshToken != null) {
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
                                    Napier.d("Token refresh successful via Refresh Token")

                                    val newAccessToken = newAuth.accessToken
                                    val newRefreshToken = newAuth.refreshToken ?: refreshToken

                                    authRepository.saveAuthData(
                                        accessToken = newAccessToken,
                                        refreshToken = newRefreshToken,
                                    )
                                    return@refreshTokens BearerTokens(newAccessToken, newRefreshToken)
                                } else {
                                    val errorBody = response.bodyAsText()
                                    Napier.e("Token refresh failed via Refresh Token: $errorBody")
                                }
                            }

                            // 2. 리프레시 토큰이 없거나 실패한 경우 Silent Sign-In 시도
                            Napier.d("Attempting silent sign-in...")
                            // Dispatchers.IO를 사용하여 메인 스레드가 아닌 곳에서 실행되도록 강제함
                            val silentAuth = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                authRepository.silentSignIn()
                            }

                            if (silentAuth != null) {
                                Napier.d("Silent sign-in successful:: $silentAuth")
                                return@refreshTokens BearerTokens(
                                    silentAuth.accessToken,
                                    silentAuth.refreshToken ?: refreshToken ?: ""
                                )
                            }


                            // 3. 모든 시도가 실패하면 로그아웃 처리
                            Napier.e("All token refresh attempts failed. Clearing auth data.")
                            authRepository.clearAuthData()
                            null
                        } catch (e: Exception) {
                            Napier.e("Exception during token refresh process", e)
                            null
                        }
                    }

                    sendWithoutRequest { request ->
                        val isGoogleApi = request.url.host.contains("googleapis.com")
                        val isTokenEndpoint = request.url.encodedPath.contains("/token")
                        isGoogleApi && !isTokenEndpoint
                    }
                }
            }
        }
    }
}
