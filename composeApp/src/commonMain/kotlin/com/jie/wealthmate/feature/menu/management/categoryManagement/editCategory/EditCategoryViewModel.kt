package com.jie.wealthmate.feature.menu.management.categoryManagement.editCategory

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.jie.wealthmate.base.BaseViewModel
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.database.eneity.CategoryEntity
import com.jie.wealthmate.database.eneity.CategoryTagEntity
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryTagVo.Companion.mapperToVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import kotlinx.coroutines.launch

class EditCategoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val largeCategory: LargeCategoryEnum,
    private val categoryId: String?,
) : BaseViewModel<EditCategoryUiState>() {

    private var categoryItems: List<CategoryVo> = emptyList()

    override val initialState: EditCategoryUiState
        get() = EditCategoryUiState(
            largeCategory = largeCategory,
            categoryId = categoryId
        )

    init {
        getCategories(largeCategory)
        categoryId?.let {
            if (it.isNotBlank()) {
                getCategory(it, largeCategory)
            }
        }
    }


    private fun getCategory(categoryId: String, largeCategory: LargeCategoryEnum) {
        viewModelScope.launch {
            val category = categoryRepository.getCategoryById(categoryId)
            if (category != null) {
                reduceState { state ->
                    state.copy(
                        largeCategory = largeCategory,
                        categoryId = categoryId,
                        categoryIcon = CategoryIconEnum.creatorFromText(category.icon),
                        label = TextFieldValue(category.middleLabel),
                        tagLabelItems = category.tags.map { it.mapperToVo() },
                        sort = category.sort,
                        isFixed = category.isFixed
                    )
                }
            }
        }
    }

    fun updateCategoryIcon(icon: CategoryIconEnum) {
        reduceState { it.copy(isDataChanged = true, categoryIcon = icon) }
    }

    fun updateCategoryLabel(textFieldValue: TextFieldValue) {
        reduceState { it.copy(isDataChanged = true, label = textFieldValue) }
    }

    fun updateCategoryTagLabel(textFieldValue: TextFieldValue) {
        val currentState = container.uiState.value
        if (currentState.tagLabel.text == textFieldValue.text) return

        if (currentState.tagLabel.text.isEmpty() && textFieldValue.text.isNotEmpty()) {
            val isAlreadyAdded = currentState.tagLabelItems.any { it.label == textFieldValue.text }
            if (isAlreadyAdded) return
        }

        reduceState { it.copy(tagLabel = textFieldValue) }
    }

    fun addCategoryTagLabel(tagLabel: TextFieldValue?) {
        tagLabel ?: return reduceState { it.copy(tagLabel = TextFieldValue()) }

        tagLabel.text.ifBlank {
            showSnackbar("상세 태그 이름을 입력해 주세요.")
            return
        }

        val categoryTag = CategoryTagVo(label = tagLabel.text)
        val isExisted = container.uiState.value.tagLabelItems.any { it.label == categoryTag.label }

        if (isExisted) {
            showSnackbar("이미 존재하는 태그 입니다.")
            return
        }

        reduceState { state ->
            state.copy(
                isDataChanged = true,
                tagLabel = TextFieldValue(),
                tagLabelItems = state.tagLabelItems + categoryTag
            )
        }
    }

    fun removeCategoryTagLabel(tag: CategoryTagVo) {
        reduceState { state ->
            val newItems = state.tagLabelItems.filterNot { 
                if (tag.id != null) it.id == tag.id else it.label == tag.label 
            }
            state.copy(
                isDataChanged = true,
                tagLabelItems = newItems
            )
        }
    }

    fun modifyCategoryTagLabel(tag: CategoryTagVo) {
        if (tag.label.isBlank()) {
            showSnackbar("태그 이름을 입력해 주세요.")
            return
        }

        reduceState { state ->
            val newItems = state.tagLabelItems.map {
                if (it.id == tag.id) tag else it
            }
            state.copy(
                isDataChanged = true,
                tagLabelItems = newItems,
            )
        }
        postSideEffect(EditCategoryUiSideEffect.OnSuccessModifyTagLabel)
    }

    fun updateIsFixed(isFixed: Boolean) {
        reduceState { it.copy(isDataChanged = true, isFixed = isFixed) }
    }

    fun saveCategory() {
        val uiState = container.uiState.value

        if (uiState.categoryId.isNullOrBlank() && categoryItems.any { it.middleLabel == uiState.label.text }) {
            showSnackbar("이미 존재하는 카테고리입니다.")
            return
        }

        viewModelScope.launch {
            showLoading(true)
            try {
                if (uiState.categoryId.isNullOrBlank()) {
                    categoryRepository.insertCategory(
                        icon = uiState.categoryIcon.text,
                        largeCategory = uiState.largeCategory.name,
                        middleLabel = uiState.label.text,
                        sort = categoryItems.size.toLong(),
                        isFixed = uiState.isFixed,
                        tagLabels = uiState.tagLabelItems.map { it.label }
                    )
                } else {
                    categoryRepository.updateCategory(
                        CategoryEntity(
                            id = uiState.categoryId,
                            icon = uiState.categoryIcon.text,
                            largeCategory = uiState.largeCategory.name,
                            middleLabel = uiState.label.text,
                            sort = uiState.sort,
                            isFixed = uiState.isFixed,
                            tags = uiState.tagLabelItems.map {
                                CategoryTagEntity(id = it.id.default(), tagLabel = it.label)
                            }
                        )
                    )
                }
                showSnackbar("카테고리가 저장되었습니다.")
                postSideEffect(EditCategoryUiSideEffect.OnSuccess)
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    fun removeCategory() {
        val id = categoryId ?: return
        viewModelScope.launch {
            showLoading(true)
            try {
                categoryRepository.deleteCategory(id)
                showSnackbar("카테고리가 삭제되었습니다.")
                postSideEffect(EditCategoryUiSideEffect.OnSuccess)
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    fun getCategories(largeCategoryEnum: LargeCategoryEnum) {
        categoryRepository.getCategoriesByLargeCategory(largeCategoryEnum.name)
            .apiFlow(showLoadingIndicator = false) { response ->
                categoryItems = response.map { it.mapperToVo() }
            }
    }
}
