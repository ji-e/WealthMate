package com.jie.wealthmate.feature.menu.management.categoryManagement

import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import kotlinx.coroutines.launch

class CategoryManagementViewModel(
    private val categoryRepository: CategoryRepository,
) : BaseViewModel<CategoryManagementUiState>() {

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
        viewModelScope.launch {
            showLoading(true)
            try {
                val categoryItems = container.uiState.value.currentCategoryItems
                categoryRepository.updateCategoriesSort(
                    categoryItems?.mapIndexed { index, item -> item.id to index.toLong() }.default()
                )
                showSnackbar("저장되었습니다.")
            } catch (e: Exception) {
                showSnackbar(e.message ?: "카테고리 저장에 실패했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    fun handleReorderCategoryItems(from: Int, to: Int) = reduceState { state ->
        val currentItems = state.currentCategoryItems.default().toMutableList()
        if (from !in currentItems.indices || to !in currentItems.indices) return@reduceState state
        
        currentItems.add(to, currentItems.removeAt(from))
        
        val newMap = state.categoryMap.toMutableMap()
        newMap[state.currentLargeCategory] = currentItems
        
        state.copy(categoryMap = newMap)
    }
}
