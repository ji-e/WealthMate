package com.jie.wealthmate.feature.home

import cafe.adriel.voyager.core.model.screenModelScope
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.base.BaseUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenModel() : BaseScreenModel<BaseUiState>() {
    private val _counter = MutableStateFlow(0)
    val counter = _counter.asStateFlow()

    fun increment() {
        screenModelScope.launch {
            _counter.value++
        }
    }

    override val initialState: BaseUiState
        get() = TODO("Not yet implemented")
}