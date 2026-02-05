package com.jie.wealthmate.entity

import kotlinx.serialization.Serializable

@Serializable
data class GoogleDrivePermissionEntity(
    val permissions: List<DrivePermission> = emptyList()
)

@Serializable
data class DrivePermission(
    val id: String,
    val type: String,
    val role: String,
    val emailAddress: String? = null,
    val displayName: String? = null
)
