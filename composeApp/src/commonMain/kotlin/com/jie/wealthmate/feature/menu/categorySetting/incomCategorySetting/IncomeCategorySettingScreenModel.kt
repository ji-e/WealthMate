package com.jie.wealthmate.feature.menu.categorySetting.incomCategorySetting

import cafe.adriel.voyager.core.model.screenModelScope
import com.jie.wealthmate.MainScreenModel
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.categorySetting.component.CategoryItemData
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import kotlinx.coroutines.launch

class IncomeCategorySettingScreenModel(
    val mainScreenModel: MainScreenModel,
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<IncomeCategorySettingUiState>() {

    override val initialState: IncomeCategorySettingUiState
        get() = IncomeCategorySettingUiState()

    init {
        getAllCategories()
    }

    fun getAllCategories() {
        screenModelScope.launch {
            val categories = categoryRepository.getAllCategoriesWithTags()

            reduceState { state ->
                state.copy(
                    incomeCategoryItems = categories.map {
                        CategoryItemData(
                            id = it.id,
                            icon = it.icon,
                            label = it.middleLabel,
                            sort = it.sort,
                            isFixed = it.fixed,
                            largeCategory = LargeCategoryEnum.creator(it.largeCategory)
                        )
                    }
                )
            }
        }
    }

    fun handleReorderImageItems(from: Int, to: Int) = reduceState { state ->
        val imageItems = state.incomeCategoryItems.toMutableList()
        state.copy(
            incomeCategoryItems = imageItems.apply { add(to, removeAt(from)) },
        )
    }
}