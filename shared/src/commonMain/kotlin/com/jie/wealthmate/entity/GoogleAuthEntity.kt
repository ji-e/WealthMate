package com.jie.wealthmate.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GoogleAuthEntity(
    @SerialName("access_token") val accessToken: String,
)
