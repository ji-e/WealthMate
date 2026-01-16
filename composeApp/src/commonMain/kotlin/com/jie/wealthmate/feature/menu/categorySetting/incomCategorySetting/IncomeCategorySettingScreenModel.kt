package com.jie.wealthmate.feature.menu.categorySetting.incomCategorySetting

import cafe.adriel.voyager.core.model.screenModelScope
import com.jie.wealthmate.MainScreenModel
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.feature.menu.categorySetting.component.CategoryItemData
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorBlue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IncomeCategorySettingScreenModel(
    val mainScreenModel: MainScreenModel
) : BaseScreenModel<IncomeCategorySettingUiState>() {
    private val _counter = MutableStateFlow(0)
    val counter = _counter.asStateFlow()

    fun increment() {
        screenModelScope.launch {
            _counter.value++
        }
    }

    override val initialState: IncomeCategorySettingUiState
        get() = IncomeCategorySettingUiState(
            incomeCategoryItems = mutableListOf<CategoryItemData>().apply {
                repeat(5) {
                    add(
                        CategoryItemData(
                            id = it,
                            icon = CategoryIconEnum.CATEGORY_U1F9D0.text,
                            label = "급여",
                            backgroundColor = ColorBlue.Blue_100,
                            sort = it + 1,
                            largeCategory = LargeCategoryEnum.INCOME
                        )
                    )
                }
            }
        )
}