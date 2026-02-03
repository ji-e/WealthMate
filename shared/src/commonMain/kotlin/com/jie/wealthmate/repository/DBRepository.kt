package com.jie.wealthmate.repository

import com.jie.wealthmate.BACK_UP_DB_NAME


interface DBRepository {
    suspend fun fetchAccessToken(authCode: String): String?

    suspend fun syncDatabaseToDrive(
        accessToken: String,
        dbBytes: ByteArray,
        fileName: String = BACK_UP_DB_NAME,
    )

    suspend fun checkAndDownloadBackup(accessToken: String)
}
