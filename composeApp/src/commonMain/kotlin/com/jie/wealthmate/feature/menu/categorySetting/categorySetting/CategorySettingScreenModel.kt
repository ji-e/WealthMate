package com.jie.wealthmate.feature.menu.categorySetting.categorySetting

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.categorySetting.component.CategoryItemData
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository

class CategorySettingScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<CategorySettingUiState>() {

    override val initialState: CategorySettingUiState
        get() = CategorySettingUiState()

    var largeCategoryEnum: LargeCategoryEnum = LargeCategoryEnum.INCOME

    fun updateInit(largeCategoryEnum: LargeCategoryEnum) {
        this.largeCategoryEnum = largeCategoryEnum

        getIncomeCategories()
    }

    fun getIncomeCategories() {
        categoryRepository.getCategoriesByLargeCategory(largeCategoryEnum.name)
            .apiFlow { response ->
                reduceState { state ->
                    state.copy(
                        isInitialized = true,
                        incomeCategoryItems = response.map {
                            CategoryItemData(
                                id = it.id,
                                icon = it.icon,
                                label = it.middleLabel,
                                sort = it.sort,
                                isFixed = it.isFixed,
                                largeCategory = LargeCategoryEnum.creator(it.largeCategory)
                            )
                        }
                    )
                }
            }
    }

    fun saveCategorySort() {
        launchSafe(
            block = {
                val categoryItems = container.uiState.value.incomeCategoryItems
                categoryRepository.updateCategoriesSort(
                    categoryItems.mapIndexed { index, item -> item.id to index.toLong() }
                )
            },
            errorMsg = "카테고리 저장에 실패했습니다.",
        ) {
            showSnackbar("저장되었습니다.")
        }
    }

    fun handleReorderCategoryItems(from: Int, to: Int) = reduceState { state ->
        val categoryItems = state.incomeCategoryItems.toMutableList()
        state.copy(
            incomeCategoryItems = categoryItems.apply { add(to, removeAt(from)) },
        )
    }
}