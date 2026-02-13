package com.jie.wealthmate

import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.component.bottomNav.BottomNavItem
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.utils.today
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

data class MainUiState(
    val title: TopBarItem.Title? = null,
    val readingItem: TopBarItem.ReadingItem? = null,
    val trailingItem: List<TopBarItem.TrailingItem>? = null,
    val trailingCustomItem: TopBarItem.TrailingCustomItem? = null,
    val selectedDate: LocalDate = today,
    val selectedItem: String = BottomNavItem.Home.route,
) : BaseUiState


object MainUiManager {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<UiSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun updateSelectedDate(date: LocalDate) {
        _uiState.update {
            it.copy(selectedDate = date)
        }
    }

    fun updateSelectedItem(route: String) {
        _uiState.update {
            it.copy(selectedItem = route)
        }
    }

    suspend fun emitSideEffect(effect: UiSideEffect) {
        println("emitSideEffect called with effect: $effect")

        _sideEffect.emit(effect)
    }
}