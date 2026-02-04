package com.jie.wealthmate.repository

import com.jie.wealthmate.BACK_UP_DB_NAME
import com.jie.wealthmate.entity.GoogleAuthEntity


interface GoogleRepository {
    suspend fun fetchGoogleAuth(authCode: String): GoogleAuthEntity?

    suspend fun uploadDatabase(
        dbBytes: ByteArray,
        fileName: String = BACK_UP_DB_NAME,
    )

    suspend fun downloadDatabase(
        fileName: String = BACK_UP_DB_NAME,
    ): ByteArray
}
