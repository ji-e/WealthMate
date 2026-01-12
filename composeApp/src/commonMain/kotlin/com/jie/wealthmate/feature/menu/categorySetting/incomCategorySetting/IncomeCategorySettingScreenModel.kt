package com.jie.wealthmate.feature.menu.categorySetting.incomCategorySetting

import cafe.adriel.voyager.core.model.screenModelScope
import com.jie.wealthmate.base.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IncomeCategorySettingScreenModel : BaseScreenModel<IncomeCategorySettingUiState>() {
    private val _counter = MutableStateFlow(0)
    val counter = _counter.asStateFlow()

    fun increment() {
        screenModelScope.launch {
            _counter.value++
        }
    }

    override val initialState: IncomeCategorySettingUiState
        get() = IncomeCategorySettingUiState()
}