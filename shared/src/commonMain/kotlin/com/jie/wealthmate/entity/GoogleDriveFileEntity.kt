package com.jie.wealthmate.entity

import kotlinx.serialization.Serializable

@Serializable
data class GoogleDriveFileEntity(
    val files: List<DriveFileEntity> = emptyList(),
    val nextPageToken: String? = null,
)

@Serializable
data class DriveFileEntity(
    val id: String,
    val name: String,
    val modifiedTime: String? = null,
    val owners: List<DriveUser>? = null,
    val capabilities: DriveFileCapabilities? = null,
)

@Serializable
data class DriveUser(
    val displayName: String? = null,
    val emailAddress: String? = null,
    val photoLink: String? = null,
)

@Serializable
data class DriveFileCapabilities(
    val canEdit: Boolean? = null,
)
