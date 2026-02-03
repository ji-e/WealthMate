package com.jie.wealthmate.feature.menu.googleCloudSync

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.repository.CategoryRepository

class GoogleCloudSyncScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<GoogleCloudSyncUiState>() {

    override val initialState: GoogleCloudSyncUiState
        get() = GoogleCloudSyncUiState()

}