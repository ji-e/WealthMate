package com.jie.wealthmate

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.component.topbar.TopBarItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MainUiState(
    val title: TopBarItem.Title? = null,
    val readingItem: TopBarItem.ReadingItem? = null,
    val trailingItem: List<TopBarItem.TrailingItem>? = null,
) : BaseUiState


object MainUiManager {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<UiSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun updateTopBar(
        title: TopBarItem.Title? = null,
        readingItem: TopBarItem.ReadingItem? = null,
        trailingItem: List<TopBarItem.TrailingItem>? = null,
    ) {
        println("updateTopBar called with title: $title, readingItem: $readingItem, trailingItem: $trailingItem")

        _uiState.update {
            it.copy(
                title = title,
                readingItem = readingItem,
                trailingItem = trailingItem
            )
        }
    }

    suspend fun emitSideEffect(effect: UiSideEffect) {
        println("emitSideEffect called with effect: $effect")

        _sideEffect.emit(effect)
    }
}