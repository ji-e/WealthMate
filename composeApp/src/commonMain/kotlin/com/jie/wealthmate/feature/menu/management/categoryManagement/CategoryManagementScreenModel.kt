package com.jie.wealthmate.feature.menu.management.categoryManagement

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.CategoryItemData
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository

class CategoryManagementScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<CategoryManagementUiState>() {

    override val initialState: CategoryManagementUiState
        get() = CategoryManagementUiState()

    fun getCategories(largeCategoryEnum: LargeCategoryEnum) {
        categoryRepository.getCategoriesByLargeCategory(largeCategoryEnum.name)
            .apiFlow { response ->
                reduceState { state ->
                    state.copy(
                        categoryItems = response.map {
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
                val categoryItems = container.uiState.value.categoryItems
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
        val categoryItems = state.categoryItems.toMutableList()
        state.copy(
            categoryItems = categoryItems.apply { add(to, removeAt(from)) },
        )
    }
}