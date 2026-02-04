package com.jie.wealthmate.feature.menu.googleCloudShare

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.repository.GoogleRepository

class GoogleCloudShareScreenModel(
    private val googleRepository: GoogleRepository,
) : BaseScreenModel<GoogleCloudShareUiState>() {

    override val initialState: GoogleCloudShareUiState
        get() = GoogleCloudShareUiState()

}
