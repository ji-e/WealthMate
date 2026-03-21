package com.jie.wealthmate.feature.menu.management.categoryManagement.modifyCategory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jie.wealthmate.base.BaseScreen
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
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryTagVo
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_delete_outline
import wealthmate.composeapp.generated.resources.ic_expand_circle_right

@Composable
fun ModifyCategoryScreen(
    navController: NavController,
    largeCategory: LargeCategoryEnum,
    categoryId: String,
    viewModel: ModifyCategoryViewModel = koinViewModel { parametersOf(largeCategory, categoryId) },
) {
    val focusManager = LocalFocusManager.current

    BaseScreen(
        viewModel = viewModel,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is ModifyCategoryUiSideEffect.OnSuccess -> {
                    navController.popBackStack()
                }

                is ModifyCategoryUiSideEffect.OnSuccessModifyTagLabel -> {
                    focusManager.clearFocus()
                }
            }
        }
    ) { uiState ->
        ModifyCategoryContent(
            uiState = uiState,
            largeCategory = largeCategory,
            onBack = {
                if (uiState.isDataChanged) {
                    // TODO: show save back dialog
                    navController.popBackStack()
                } else {
                    navController.popBackStack()
                }
            },
            onRemoveCategory = viewModel::removeCategory,
            onUpdateCategoryIcon = viewModel::updateCategoryIcon,
            onUpdateCategoryLabel = viewModel::updateCategoryLabel,
            onUpdateCategoryTagLabel = viewModel::updateCategoryTagLabel,
            onAddCategoryTagLabel = viewModel::addCategoryTagLabel,
            onSelectedCategoryTagLabel = viewModel::selectedCategoryTagLabel,
            onUpdateModifyCategoryTagLabel = viewModel::updateModifyCategoryTagLabel,
            onRemoveCategoryTagLabel = viewModel::removeCategoryTagLabel,
            onModifyCategoryTagLabel = viewModel::modifyCategoryTagLabel,
            onUpdateIsFixed = viewModel::updateIsFixed,
            onSaveCategory = viewModel::saveCategory
        )
    }
}

@Composable
fun ModifyCategoryContent(
    uiState: ModifyCategoryUiState,
    largeCategory: LargeCategoryEnum,
    onBack: () -> Unit,
    onRemoveCategory: () -> Unit,
    onUpdateCategoryIcon: (CategoryIconEnum) -> Unit,
    onUpdateCategoryLabel: (TextFieldValue) -> Unit,
    onUpdateCategoryTagLabel: (TextFieldValue) -> Unit,
    onAddCategoryTagLabel: (TextFieldValue?) -> Unit,
    onSelectedCategoryTagLabel: (CategoryTagVo) -> Unit,
    onUpdateModifyCategoryTagLabel: (String) -> Unit,
    onRemoveCategoryTagLabel: () -> Unit,
    onModifyCategoryTagLabel: () -> Unit,
    onUpdateIsFixed: (Boolean) -> Unit,
    onSaveCategory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowCategoryIconModalBottomSheet by remember { mutableStateOf(false) }
    var isShowCategoryTagLabelModalBottomSheet by remember { mutableStateOf(false) }
    var isShowSaveBackDialog by remember { mutableStateOf(false) }
    var isShowRemoveDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    var isKeyboardOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        WMTopBar(
            title = TopBarItem.Title("${largeCategory.label} 카테고리 수정"),
            readingItem = TopBarItem.ReadingItem(action = onBack),
            trailingItem = listOf(
                TopBarItem.TrailingItem(
                    iconRes = Res.drawable.ic_delete_outline,
                    action = { isShowRemoveDialog = true }
                )
            )
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CategoryIcon(
                modifier = Modifier.padding(top = 32.dp),
                largeCategory = largeCategory,
                selectedCategoryIcon = uiState.categoryIcon,
                onClickChange = {
                    focusManager.clearFocus()
                    isShowCategoryIconModalBottomSheet = true
                }
            )

            WMTextField(
                value = uiState.label,
                onValueChange = onUpdateCategoryLabel,
                modifier = Modifier.padding(top = 32.dp),
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

            CategoryTag(
                modifier = Modifier.padding(top = 4.dp),
                textFieldModifier = Modifier.focusRequester(remember { FocusRequester() })
                    .onFocusChanged { focusState ->
                        isKeyboardOpen = focusState.isFocused
                    },
                largeCategory = largeCategory,
                tagLabel = uiState.tagLabel,
                trailingIcon = Res.drawable.ic_expand_circle_right,
                tagLabelItems = uiState.tagLabelItems,
                onValueChange = onUpdateCategoryTagLabel,
                onChipAdd = onAddCategoryTagLabel,
                onChipClick = {
                    onSelectedCategoryTagLabel(it)
                    isShowCategoryTagLabelModalBottomSheet = true
                }
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        WMCheckBox(
            label = "고정 카테고리",
            checked = uiState.isFixed,
            modifier = Modifier
                .padding(start = 28.dp)
                .padding(vertical = 16.dp)
                .align(Alignment.Start),
            onCheckedChange = onUpdateIsFixed,
        )

        WMButton(
            text = "수정",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            enabled = uiState.isDataChanged,
            onClick = onSaveCategory
        )

        if (isShowCategoryTagLabelModalBottomSheet) {
            CategoryTagLabelModalBottomSheet(
                isKeyboardOpen = isKeyboardOpen,
                tagLabel = uiState.modifyTagLabel?.label.default(),
                selectedTagLabel = uiState.selectedTagLabel,
                onTagLabelChange = onUpdateModifyCategoryTagLabel,
                onRemoveClick = {
                    onRemoveCategoryTagLabel()
                    isShowCategoryTagLabelModalBottomSheet = false
                },
                onModifyClick = {
                    onModifyCategoryTagLabel()
                    isShowCategoryTagLabelModalBottomSheet = false
                },
                onDismissRequest = { isShowCategoryTagLabelModalBottomSheet = false }
            )
        }

        if (isShowCategoryIconModalBottomSheet) {
            CategoryIconModalBottomSheet(
                selectedCategoryIcon = uiState.categoryIcon,
                onIconChange = onUpdateCategoryIcon,
                onDismissRequest = { isShowCategoryIconModalBottomSheet = false }
            )
        }

        // TODO: Handle isShowRemoveDialog with a standard BaseScreen dialog if available, or locally.
    }
}
