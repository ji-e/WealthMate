package com.jie.wealthmate.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Tasks
import com.jie.wealthmate.entity.GoogleAuthEntity
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual suspend fun platformSilentSignIn(): GoogleAuthEntity? {
    return try {
        // Koin을 통해 Android Context 획득
        val koinContext = object : KoinComponent {
            val context: Context by inject()
        }.context
        
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(GoogleRepositoryImpl.CLIENT_ID)
            .requestScopes(Scope("https://www.googleapis.com/auth/drive.appdata"))
            .requestScopes(Scope("https://www.googleapis.com/auth/drive.file"))
            .build()

        val client = GoogleSignIn.getClient(koinContext, gso)
        
        // Silent Sign-In 시도 (동기 대기)
        val task = client.silentSignIn()
        val account: GoogleSignInAccount = Tasks.await(task)
        val authCode = account.serverAuthCode

        if (authCode != null) {
            Napier.d("Android: Silent sign-in success, exchanging authCode...")
            exchangeAuthCode(authCode)
        } else {
            Napier.w("Android: Silent sign-in success but authCode is null")
            null
        }
    } catch (e: Exception) {
        Napier.e("Android: Silent sign-in failed", e)
        null
    }
}

private suspend fun exchangeAuthCode(authCode: String): GoogleAuthEntity? {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    return try {
        val response = client.post("https://oauth2.googleapis.com/token") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append("grant_type", "authorization_code")
                append("code", authCode)
                append("client_id", GoogleRepositoryImpl.CLIENT_ID)
                append("client_secret", GoogleRepositoryImpl.CLIENT_SECRET)
                append("redirect_uri", "")
            }))
        }
        
        if (response.status.value == 200) {
            response.body<GoogleAuthEntity>()
        } else {
            val errorBody = response.body<String>()
            Napier.e("Android: Token exchange failed: $errorBody")
            null
        }
    } catch (e: Exception) {
        Napier.e("Android: Token exchange exception", e)
        null
    } finally {
        client.close()
    }
}
