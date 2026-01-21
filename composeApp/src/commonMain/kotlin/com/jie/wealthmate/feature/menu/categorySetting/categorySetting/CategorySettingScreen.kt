@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.menu.categorySetting.categorySetting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.reorderable.ReorderableItem
import com.jie.wealthmate.component.reorderable.detectReorderAfterLongPress
import com.jie.wealthmate.component.reorderable.rememberReorderableLazyListState
import com.jie.wealthmate.component.reorderable.reorderable
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.menu.categorySetting.addCategory.AddCategoryScreen
import com.jie.wealthmate.feature.menu.categorySetting.component.CategoryItem
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.feature.menu.categorySetting.modifyCategory.ModifyCategoryScreen
import com.jie.wealthmate.feature.menu.component.MenuEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_add

class CategorySettingScreen(
    val menuEnum: MenuEnum,
) : BaseScreen() {
    private val largeCategoryEnum = LargeCategoryEnum.creatorFromMenu(menuEnum.label)

    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: CategorySettingScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value

        var isDragging by remember { mutableStateOf(false) }
        val hapticFeedback = LocalHapticFeedback.current
        val listState = rememberReorderableLazyListState(
            onMove = { from, to -> screenModel.handleReorderCategoryItems(from.index, to.index) }
        )

        fun onBack() {
            if (isDragging) {
                showSaveBackDialog(isDragging) {
                    isDragging = false
                    screenModel.getIncomeCategories()
                }
            } else {
                navigator.pop()
            }
        }


        BackHandler(true) {
            onBack()
        }

        if (navigator.lastItem is CategorySettingScreen) {
            SideEffect {
                if (isDragging) {
                    screenModel.updateTopBar(
                        title = TopBarItem.Title("${menuEnum.label} 순서 변경"),
                        readingItem = TopBarItem.ReadingItem().copy(
                            action = { onBack() }
                        )
                    )
                } else if (uiState.isInitialized) {
                    val isAddItemEnabled = uiState.incomeCategoryItems.size < 10
                    screenModel.updateTopBar(
                        title = TopBarItem.Title(menuEnum.title),
                        readingItem = TopBarItem.ReadingItem().copy(
                            action = { navigator.pop() }
                        ),
                        trailingItem = listOf(
                            TopBarItem.TrailingItem(
                                iconRes = Res.drawable.ic_add,
                                tint = if (isAddItemEnabled) ColorGray.Gray_700 else ColorGray.Gray_100,
                                action = {
                                    if (isAddItemEnabled.not()) return@TrailingItem
                                    navigator.push(
                                        AddCategoryScreen(
                                            largeCategory = largeCategoryEnum,
                                            categoryItems = uiState.incomeCategoryItems
                                        )
                                    )
                                }
                            )
                        )
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            screenModel.updateInit(largeCategoryEnum)
        }

        Column {
            val itemSize = uiState.incomeCategoryItems.size

            if (itemSize == 0) {
                EmptyListView(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    contentText = "카테고리를 추가해주세요.",
                )
                return@Column
            }

            LazyColumn(
                state = listState.listState,
                modifier = Modifier
                    .weight(1f)
                    .reorderable(listState),
            ) {


                items(
                    count = itemSize,
                    key = { index -> uiState.incomeCategoryItems[index].id }
                ) { index ->
                    val category = uiState.incomeCategoryItems[index]
                    ReorderableItem(
                        state = listState,
                        key = category.id,
                    ) {
                        if (isDragging.not()) {
                            isDragging = it
                        }

                        if (it) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }

                        CategoryItem(
                            data = category,
                            modifier = Modifier.then(
                                if (it) {
                                    Modifier
                                        .padding(horizontal = 12.dp)
                                        .dropShadow(
                                            shape = RoundedCornerShape(4.dp),
                                            shadow = Shadow(
                                                radius = 10.dp,
                                                spread = 10.dp,
                                                color = ColorPrimary.Primary_200,
                                                offset = DpOffset(x = 4.dp, 4.dp)
                                            )
                                        )
                                        .clip(RoundedCornerShape(4.dp))
                                } else {
                                    Modifier.clickable(isDragging.not()) {
                                        goToModifyCategory(
                                            navigator = navigator,
                                            categoryId = category.id
                                        )
                                    }
                                }
                            ),
                            onDragHandle = Modifier.detectReorderAfterLongPress(listState),
                        )
                    }
                }
            }

            // Drag and Drop 저장 버튼
            if (isDragging) {
                WMFloatingButton(
                    text = "저장",
                    buttonSize = ButtonSize.LARGE,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                        .fillMaxWidth(),
                    onClick = {
                        screenModel.saveCategorySort()
                        isDragging = false
                    }
                )
            }
        }
    }

    fun goToModifyCategory(navigator: Navigator, categoryId: Long) {
        navigator.push(
            ModifyCategoryScreen(
                largeCategory = largeCategoryEnum,
                categoryId = categoryId
            )
        )
    }

    @Composable
    @Preview(showBackground = true)
    private fun IncomeCategorySettingScreenPreview() {
        WMTheme {
            CategorySettingScreen(MenuEnum.INCOME_CATEGORY)
        }
    }
}