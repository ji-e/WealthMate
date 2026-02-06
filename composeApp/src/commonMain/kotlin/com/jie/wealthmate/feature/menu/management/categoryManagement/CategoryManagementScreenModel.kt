package com.jie.wealthmate.feature.menu.management.categoryManagement

import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo

class CategoryManagementScreenModel(
    private val categoryRepository: CategoryRepository,
) : BaseScreenModel<CategoryManagementUiState>() {

    override val initialState: CategoryManagementUiState
        get() = CategoryManagementUiState()

    fun getCategories(largeCategoryEnum: LargeCategoryEnum) {
        categoryRepository.getCategoriesByLargeCategory(largeCategoryEnum.name)
            .apiFlow { response ->
                reduceState { state ->
                    val newMap = state.categoryMap.toMutableMap()
                    newMap[largeCategoryEnum] = response.map { it.mapperToVo() }
                    state.copy(
                        categoryMap = newMap,
                        currentLargeCategory = largeCategoryEnum
                    )
                }
            }
    }

    fun changeTab(largeCategoryEnum: LargeCategoryEnum) {
        reduceState { state ->
            state.copy(currentLargeCategory = largeCategoryEnum)
        }
        if (container.uiState.value.categoryMap[largeCategoryEnum] == null) {
            getCategories(largeCategoryEnum)
        }
    }

    fun saveCategorySort() {
        launchSafe(
            block = {
                val categoryItems = container.uiState.value.currentCategoryItems
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
        val currentItems = state.currentCategoryItems.toMutableList()
        if (from !in currentItems.indices || to !in currentItems.indices) return@reduceState state
        
        currentItems.add(to, currentItems.removeAt(from))
        
        val newMap = state.categoryMap.toMutableMap()
        newMap[state.currentLargeCategory] = currentItems
        
        state.copy(categoryMap = newMap)
    }
}
