package com.jie.wealthmate.repository

import com.jie.wealthmate.BACK_UP_DB_NAME


interface GoogleRepository {
    suspend fun fetchAccessToken(authCode: String): String?

    suspend fun uploadDatabase(
        accessToken: String,
        dbBytes: ByteArray,
        fileName: String = BACK_UP_DB_NAME,
    )

    suspend fun downloadDatabase(
        accessToken: String,
        fileName: String = BACK_UP_DB_NAME,
    ): ByteArray
}
