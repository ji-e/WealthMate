package com.jie.wealthmate.feature.menu.management.categoryManagement.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.component.reorderable.ItemPosition
import com.jie.wealthmate.feature.menu.management.categoryManagement.CategoryManagementUiState
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import kotlinx.coroutines.launch

class CategoryManagementViewModel(
    private val categoryRepository: CategoryRepository,
) : BaseViewModel<CategoryManagementUiState>() {

    var categoryMap by mutableStateOf<Map<LargeCategoryEnum, List<CategoryVo>>>(emptyMap())
        private set

    val currentCategoryItems: List<CategoryVo>?
        get() = categoryMap[container.uiState.value.currentLargeCategory]

    override val initialState: CategoryManagementUiState
        get() = CategoryManagementUiState()

    fun getCategories(largeCategoryEnum: LargeCategoryEnum) {
        categoryRepository.getCategoriesByLargeCategory(largeCategoryEnum.name)
            .apiFlow { response ->
                val newMap = categoryMap.toMutableMap()
                newMap[largeCategoryEnum] = response.map { it.mapperToVo() }
                categoryMap = newMap

                reduceState { state ->
                    state.copy(currentLargeCategory = largeCategoryEnum)
                }
            }
    }

    fun changeTab(largeCategoryEnum: LargeCategoryEnum) {
        reduceState { state ->
            state.copy(currentLargeCategory = largeCategoryEnum)
        }
        if (categoryMap[largeCategoryEnum] == null) {
            getCategories(largeCategoryEnum)
        }
    }

    fun saveCategorySort() {
        viewModelScope.launch {
            showLoading(true)
            try {
                categoryRepository.updateCategoriesSort(
                    currentCategoryItems?.mapIndexed { index, item -> item.id to index.toLong() }.default()
                )
                showSnackbar("저장되었습니다.")
            } catch (e: Exception) {
                showSnackbar(e.message ?: "카테고리 저장에 실패했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    fun handleReorderCategoryItems(from: ItemPosition, to: ItemPosition) {
        val currentItems = currentCategoryItems.default().toMutableList()
        val fromIndex = currentItems.indexOfFirst { it.id == from.key }
        val toIndex = currentItems.indexOfFirst { it.id == to.key }

        if (fromIndex != -1 && toIndex != -1) {
            currentItems.add(toIndex, currentItems.removeAt(fromIndex))

            val newMap = categoryMap.toMutableMap()
            newMap[container.uiState.value.currentLargeCategory] = currentItems
            categoryMap = newMap
        }
    }

    fun onDragOver(draggedOver: ItemPosition): Boolean {
        return currentCategoryItems?.any { it.id == draggedOver.key } ?: false
    }
}
