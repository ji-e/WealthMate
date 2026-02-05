package com.jie.wealthmate.vo

import com.jie.wealthmate.entity.GoogleDrivePermissionEntity
import com.jie.wealthmate.utils.default


data class GoogleDrivePermissionVo(
    val permissions: List<DrivePermission> = emptyList(),
) {
    companion object {
        fun GoogleDrivePermissionEntity?.mapperToVo() = GoogleDrivePermissionVo(
            permissions = this?.permissions?.map {
                DrivePermission(
                    id = it.id,
                    type = it.type,
                    role = it.role,
                    emailAddress = it.emailAddress,
                    displayName = it.displayName
                )
            }.default().sortedBy { it.role }
        )
    }
}


data class DrivePermission(
    val id: String,
    val type: String,
    val role: String,
    val emailAddress: String? = null,
    val displayName: String? = null,
)
