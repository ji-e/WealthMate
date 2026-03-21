package com.jie.wealthmate.feature.menu.management.categoryManagement.modifyCategory

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
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import kotlinx.coroutines.launch

class ModifyCategoryViewModel(
    private val categoryRepository: CategoryRepository,
) : BaseViewModel<ModifyCategoryUiState>() {

    private var originalCategory: CategoryVo? = null

    override val initialState: ModifyCategoryUiState
        get() = ModifyCategoryUiState()

    fun updateInit(largeCategoryEnum: LargeCategoryEnum, categoryId: String) {
        getCategory(categoryId, largeCategoryEnum)
    }

    private fun getCategory(categoryId: String, largeCategoryEnum: LargeCategoryEnum) {
        viewModelScope.launch {
            val category = categoryRepository.getCategoryById(categoryId)
            if (category != null) {
                originalCategory = category.mapperToVo(largeCategoryEnum)
                originalCategory?.let { vo ->
                    reduceState { state ->
                        state.copy(
                            largeCategory = largeCategoryEnum,
                            categoryId = categoryId,
                            categoryIcon = CategoryIconEnum.creatorFromText(vo.icon),
                            label = TextFieldValue(vo.middleLabel),
                            tagLabelItems = vo.tags,
                            isFixed = vo.isFixed
                        )
                    }
                }
            }
        }
    }

    fun updateCategoryIcon(icon: CategoryIconEnum) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                categoryIcon = icon
            )
        }
    }

    fun updateCategoryLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                label = textFieldValue
            )
        }
    }

    fun updateCategoryTagLabel(textFieldValue: TextFieldValue) {
        reduceState { state ->
            state.copy(
                tagLabel = textFieldValue
            )
        }
    }

    fun addCategoryTagLabel(tagLabel: TextFieldValue?) {
        tagLabel ?: return reduceState { it.copy(tagLabel = TextFieldValue()) }

        if (tagLabel.text.isBlank()) {
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

    fun updateModifyCategoryTagLabel(label: String) {
        reduceState { state ->
            state.copy(
                modifyTagLabel = state.modifyTagLabel?.copy(label = label)
            )
        }
    }

    fun selectedCategoryTagLabel(tag: CategoryTagVo) {
        reduceState { state ->
            state.copy(
                selectedTagLabel = tag.label,
                modifyTagLabel = tag
            )
        }
    }

    fun removeCategoryTagLabel() {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                tagLabelItems = state.tagLabelItems.filter { it.label != state.selectedTagLabel },
                modifyTagLabel = null
            )
        }
        postSideEffect(ModifyCategoryUiSideEffect.OnSuccessModifyTagLabel)
    }

    fun modifyCategoryTagLabel() {
        val tag = container.uiState.value.modifyTagLabel ?: return
        val selectedTagLabel = container.uiState.value.selectedTagLabel

        if (tag.label.isBlank()) {
            showSnackbar("태그 이름을 입력해 주세요.")
            return
        }

        reduceState { state ->
            val newItems = state.tagLabelItems.map {
                if (it.label == selectedTagLabel) tag else it
            }
            state.copy(
                isDataChanged = true,
                tagLabelItems = newItems,
                modifyTagLabel = null
            )
        }
        postSideEffect(ModifyCategoryUiSideEffect.OnSuccessModifyTagLabel)
    }

    fun updateIsFixed(isFixed: Boolean) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                isFixed = isFixed
            )
        }
    }

    fun saveCategory() {
        val uiState = container.uiState.value
        viewModelScope.launch {
            showLoading(true)
            try {
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
                showSnackbar("카테고리가 수정되었습니다.")
                postSideEffect(ModifyCategoryUiSideEffect.OnSuccess)
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }

    fun removeCategory() {
        val uiState = container.uiState.value
        viewModelScope.launch {
            showLoading(true)
            try {
                categoryRepository.deleteCategory(uiState.categoryId)
                showSnackbar("카테고리가 삭제되었습니다.")
                postSideEffect(ModifyCategoryUiSideEffect.OnSuccess)
            } catch (e: Exception) {
                showSnackbar(e.message ?: "오류가 발생했습니다.")
            } finally {
                showLoading(false)
            }
        }
    }
}
