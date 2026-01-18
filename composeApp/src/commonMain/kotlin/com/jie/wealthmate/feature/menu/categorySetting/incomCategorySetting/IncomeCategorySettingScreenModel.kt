package com.jie.wealthmate.feature.menu.categorySetting.incomCategorySetting

import androidx.compose.ui.util.fastFilteredMap
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
        getIncomeCategories()
    }

    fun getIncomeCategories() {
        screenModelScope.launch {
            val categories = categoryRepository.getAllCategoriesWithTags().sortedBy { it.sort }

            reduceState { state ->
                state.copy(
                    initialized = true,
                    incomeCategoryItems = categories.fastFilteredMap(
                        { it.largeCategory == LargeCategoryEnum.INCOME.name },
                    ) {
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

    fun saveCategorySort() {
        screenModelScope.launch {
            val categoryItems = container.uiState.value.incomeCategoryItems
            categoryRepository.updateCategorySorts(
                categoryItems.mapIndexed { index, item -> item.id to index.toLong() }
            )
            mainScreenModel.showSnackbar("저장되었습니다.")
        }
    }

    fun handleReorderCategoryItems(from: Int, to: Int) = reduceState { state ->
        val categoryItems = state.incomeCategoryItems.toMutableList()
        state.copy(
            incomeCategoryItems = categoryItems.apply { add(to, removeAt(from)) },
        )
    }
}