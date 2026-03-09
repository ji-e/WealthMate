package com.jie.wealthmate.feature.search

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import kotlinx.collections.immutable.persistentListOf

class SearchScreenModel : BaseScreenModel<SearchUiState>() {
    override val initialState: SearchUiState = SearchUiState()

    fun updateQuery(query: TextFieldValue) {
        reduceState { it.copy(query = query) }
        // TODO: Implement search logic
    }

    fun clearQuery() {
        reduceState { it.copy(query = TextFieldValue(""), searchResults = persistentListOf()) }
    }

    fun search() {
        // TODO: Implement search with current query
    }
}
