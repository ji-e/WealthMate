package com.jie.wealthmate.feature.search

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseUiState
import com.jie.wealthmate.base.UiSideEffect
import com.jie.wealthmate.vo.HistoryVo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class SearchUiState(
    val query: TextFieldValue = TextFieldValue(""),
    val searchResults: ImmutableList<HistoryVo> = persistentListOf(),
) : BaseUiState

sealed class SearchUiSideEffect : UiSideEffect {
    data object OnBack : SearchUiSideEffect()
}
