package com.jie.wealthmate.feature.menu.management.categoryManagement.addCategory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.jie.wealthmate.vo.CategoryTagVo
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddCategoryScreen(
    navController: NavController,
    largeCategory: LargeCategoryEnum,
    viewModel: AddCategoryViewModel = koinViewModel(),
) {
    BaseScreen(
        viewModel = viewModel,
        onSideEffect = { sideEffect ->
            when (sideEffect) {
                is AddCategoryUiSideEffect.OnSuccessSave -> {
                    navController.popBackStack()
                }
            }
        }
    ) { uiState ->
        AddCategoryContent(
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
            onUpdateInit = viewModel::updateInit,
            onUpdateCategoryLabel = viewModel::updateCategoryLabel,
            onUpdateCategoryIcon = viewModel::updateCategoryIcon,
            onUpdateCategoryTagLabel = viewModel::updateCategoryTagLabel,
            onAddCategoryTagLabel = viewModel::addCategoryTagLabel,
            onRemoveCategoryTagLabel = viewModel::removeCategoryTagLabel,
            onUpdateIsFixed = viewModel::updateIsFixed,
            onSaveCategory = viewModel::saveCategory
        )
    }
}

@Composable
fun AddCategoryContent(
    uiState: AddCategoryUiState,
    largeCategory: LargeCategoryEnum,
    onBack: () -> Unit,
    onUpdateInit: (LargeCategoryEnum) -> Unit,
    onUpdateCategoryLabel: (TextFieldValue) -> Unit,
    onUpdateCategoryIcon: (CategoryIconEnum) -> Unit,
    onUpdateCategoryTagLabel: (TextFieldValue) -> Unit,
    onAddCategoryTagLabel: (TextFieldValue?) -> Unit,
    onRemoveCategoryTagLabel: (CategoryTagVo) -> Unit,
    onUpdateIsFixed: (Boolean) -> Unit,
    onSaveCategory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowCategoryIconModalBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onUpdateInit(largeCategory)
    }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        WMTopBar(
            title = TopBarItem.Title("${largeCategory.label} 카테고리 추가"),
            readingItem = TopBarItem.ReadingItem(action = onBack),
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
                onClickChange = { isShowCategoryIconModalBottomSheet = true }
            )

            WMTextField(
                value = uiState.label,
                onValueChange = onUpdateCategoryLabel,
                modifier = Modifier.padding(top = 32.dp),
                maxLength = 15,
                label = "카테고리 이름",
                placeholder = largeCategory.tempMiddleCategoryLabel,
                isCount = true,
                isRequire = true,
            )

            CategoryTag(
                modifier = Modifier.padding(top = 4.dp),
                largeCategory = largeCategory,
                tagLabel = uiState.tagLabel,
                tagLabelItems = uiState.tagLabelItems,
                onValueChange = onUpdateCategoryTagLabel,
                onChipAdd = onAddCategoryTagLabel,
                onChipClick = onRemoveCategoryTagLabel,
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
            text = "저장",
            buttonSize = ButtonSize.LARGE,
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            enabled = uiState.label.text.isNotBlank(),
            onClick = onSaveCategory
        )

        if (isShowCategoryIconModalBottomSheet) {
            CategoryIconModalBottomSheet(
                selectedCategoryIcon = uiState.categoryIcon,
                onIconChange = onUpdateCategoryIcon,
                onDismissRequest = { isShowCategoryIconModalBottomSheet = false }
            )
        }
    }
}
