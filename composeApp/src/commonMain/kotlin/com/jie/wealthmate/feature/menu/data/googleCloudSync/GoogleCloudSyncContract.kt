package com.jie.wealthmate.feature.menu.data.googleCloudSync

import com.jie.wealthmate.base.BaseUiState

data class GoogleCloudSyncUiState(
    val isLoggedIn: Boolean = false,
    val userName: String = "",
    val lastSyncDate: String = "",
) : BaseUiState
