@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.categorySetting.addCategory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMButton
import com.jie.wealthmate.component.WMTextField
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTobBar
import com.jie.wealthmate.feature.menu.categorySetting.addCategory.component.CategoryIcon
import com.jie.wealthmate.feature.menu.categorySetting.addCategory.component.CategoryTag
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

class AddCategoryScreen(
    val largeCategory: LargeCategoryEnum,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { AddCategoryScreenModel() }
        val uiState = screenModel.container.uiState.collectAsState().value

        BackHandler(true) {
            navigator.pop()
        }

        Scaffold(
            topBar = {
                WMTobBar(
                    title = TopBarItem.Title("${largeCategory.label} 카테고리 추가"),
                    readingItem = TopBarItem.ReadingItem().copy(
                        action = { navigator.pop() }
                    ),
                )
            }
        ) { innerPadding: PaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 카테고리 아이콘
                CategoryIcon(
                    modifier = Modifier.padding(top = 24.dp),
                    largeCategory = largeCategory,
                    selectedCategoryIcon = uiState.categoryIcon,
                    onClickChange = {}
                )

                // 카테고리 라벨
                WMTextField(
                    value = uiState.label,
                    onValueChange = screenModel::updateCategoryLabel,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .padding(horizontal = 4.dp),
                    maxLength = 15,
                    label = "카테고리 이름",
                    placeholder = largeCategory.tempMiddleCategoryLabel,
                    supportingText = "15자 이내로 입력해 주세요.",
                    isCount = true,
                    isRequire = true,
                )

                // 카테고리 태그
                CategoryTag(
                    modifier = Modifier.padding(top = 20.dp),
                    largeCategory = largeCategory,
                    tagLabel = uiState.tagLabel,
                    tagLabelItems = listOf("상여금", "식대"),
                    onValueChange = {},
                    onClickChip = {}
                )

                Spacer(modifier = Modifier.weight(1f))

                // 저장 버튼
                WMButton(
                    text = "저장",
                    buttonSize = ButtonSize.LARGE,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    onClick = {}
                )
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    private fun AddCategoryScreenPreview() {
        WMTheme {
            AddCategoryScreen(LargeCategoryEnum.INCOME)
        }
    }
}