@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.management.categoryManagement.modifyCategory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.base.collectSideEffect
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMCheckBox
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory.component.CategoryIcon
import com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory.component.CategoryIconModalBottomSheet
import com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory.component.CategoryTag
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.modifyCategory.component.CategoryTagLabelModalBottomSheet
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.default
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_delete
import wealthmate.composeapp.generated.resources.ic_expand_circle_right

class ModifyCategoryScreen(
    val largeCategory: LargeCategoryEnum,
    val categoryId: String,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: ModifyCategoryScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value

        var isShowCategoryIconModalBottomSheet by remember { mutableStateOf(false) }
        var isShowCategoryTagLabelModalBottomSheet by remember { mutableStateOf(false) }

        val focusManager = LocalFocusManager.current
        var isKeyboardOpen by remember { mutableStateOf(false) }

        val onBack: () -> Unit = remember(uiState.isDataChanged) {
            {
                showSaveBackDialog(
                    isShow = uiState.isDataChanged,
                    callback = { navigator.pop() }
                )
            }
        }

        BackHandler(
            enabled = true,
            onBack = onBack
        )

        LaunchedEffect(Unit) {
            screenModel.updateInit(
                largeCategoryEnum = largeCategory,
                categoryId = categoryId
            )
        }

        screenModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is ModifyCategoryUiSideEffect.OnSuccess -> {
                    navigator.pop()
                }

                is ModifyCategoryUiSideEffect.OnSuccessModifyTagLabel -> {
                    focusManager.clearFocus()
                    isShowCategoryTagLabelModalBottomSheet = false
                }
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            WMTopBar(
                title = TopBarItem.Title("${largeCategory.label} 카테고리 수정"),
                readingItem = TopBarItem.ReadingItem().copy(
                    action = { onBack() }
                ),
                trailingItem = listOf(
                    TopBarItem.TrailingItem(
                        iconRes = Res.drawable.ic_delete,
                        tint = ColorRed.Red_300,
                        action = {
                            showRemoveDialog() {
                                screenModel.removeCategory()
                            }
                        }
                    )
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 카테고리 아이콘
                CategoryIcon(
                    modifier = Modifier.padding(top = 32.dp),
                    largeCategory = largeCategory,
                    selectedCategoryIcon = uiState.categoryIcon,
                    onClickChange = {
                        focusManager.clearFocus()
                        isShowCategoryIconModalBottomSheet = true
                    }
                )

                // 카테고리 이름
                WMTextField(
                    value = uiState.label,
                    onValueChange = screenModel::updateCategoryLabel,
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .padding(horizontal = 28.dp),
                    textFieldModifier = Modifier.focusRequester(remember { FocusRequester() })
                        .onFocusChanged { focusState ->
                            isKeyboardOpen = focusState.isFocused
                        },
                    maxLength = 15,
                    label = "카테고리 이름",
                    placeholder = largeCategory.tempMiddleCategoryLabel,
                    isCount = true,
                    isRequire = true,
                )

                // 카테고리 태그
                CategoryTag(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .padding(horizontal = 28.dp),
                    textFieldModifier = Modifier.focusRequester(remember { FocusRequester() })
                        .onFocusChanged { focusState ->
                            isKeyboardOpen = focusState.isFocused
                        },
                    largeCategory = largeCategory,
                    tagLabel = uiState.tagLabel,
                    trailingIcon = Res.drawable.ic_expand_circle_right,
                    tagLabelItems = uiState.tagLabelItems,
                    onValueChange = screenModel::updateCategoryTagLabel,
                    onChipAdd = screenModel::addCategoryTagLabel,
                    onChipClick = {
                        screenModel.selectedCategoryTagLabel(it)
                        isShowCategoryTagLabelModalBottomSheet = true
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            // 고정 카테고리
            WMCheckBox(
                label = "고정 카테고리",
                checked = uiState.isFixed,
                modifier = Modifier
                    .padding(start = 14.dp)
                    .padding(vertical = 8.dp)
                    .align(Alignment.Start),
                onCheckedChange = screenModel::updateIsFixed,
            )

            // 수정 버튼
            WMButton(
                text = "수정",
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                enabled = uiState.isDataChanged,
                onClick = screenModel::saveCategory
            )

            if (isShowCategoryTagLabelModalBottomSheet) {
                ShowCategoryTagLabelModalBottomSheet(
                    isKeyboardOpen = isKeyboardOpen,
                    tagLabel = uiState.modifyTagLabel?.label.default(),
                    selectedTagLabel = uiState.selectedTagLabel,
                    onTagLabelChange = screenModel::updateModifyCategoryTagLabel,
                    onRemoveClick = screenModel::removeCategoryTagLabel,
                    onModifyClick = screenModel::modifyCategoryTagLabel,
                    onDismissRequest = { isShowCategoryTagLabelModalBottomSheet = false }
                )
            }

            if (isShowCategoryIconModalBottomSheet) {
                ShowCategoryIconModalBottomSheet(
                    selectedCategoryIcon = uiState.categoryIcon,
                    onIconChange = screenModel::updateCategoryIcon,
                    onDismissRequest = { isShowCategoryIconModalBottomSheet = false }
                )
            }
        }
    }

    /**
     * 카테고리 상세 태그 수정 ModalBottomSheet
     */
    @Composable
    private fun ShowCategoryTagLabelModalBottomSheet(
        isKeyboardOpen: Boolean,
        tagLabel: String,
        selectedTagLabel: String,
        onTagLabelChange: (String) -> Unit,
        onRemoveClick: () -> Unit = {},
        onModifyClick: () -> Unit = {},
        onDismissRequest: () -> Unit = {},
    ) {
        CategoryTagLabelModalBottomSheet(
            isKeyboardOpen = isKeyboardOpen,
            tagLabel = tagLabel,
            selectedTagLabel = selectedTagLabel,
            onTagLabelChange = onTagLabelChange,
            onRemoveClick = onRemoveClick,
            onModifyClick = onModifyClick,
            onDismissRequest = onDismissRequest
        )
    }

    /**
     * 아이콘 변경 ModalBottomSheet
     */

    @Composable
    private fun ShowCategoryIconModalBottomSheet(
        selectedCategoryIcon: CategoryIconEnum,
        onIconChange: (CategoryIconEnum) -> Unit = {},
        onDismissRequest: () -> Unit = {},
    ) {
        CategoryIconModalBottomSheet(
            selectedCategoryIcon = selectedCategoryIcon,
            onIconChange = onIconChange,
            onDismissRequest = onDismissRequest
        )
    }

    @Composable
    @Preview(showBackground = true)
    private fun ModifyCategoryScreenPreview() {
        WMTheme {
            ModifyCategoryScreen(
                largeCategory = LargeCategoryEnum.INCOME,
                categoryId = "0"
            )
        }
    }
}
