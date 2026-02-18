@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.budget.addBudget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.ButtonSize
import com.jie.wealthmate.component.WMCheckBox
import com.jie.wealthmate.component.WMFloatingButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.textField.rememberIntegerVisualTransformation
import com.jie.wealthmate.component.textField.toIntegerTextFieldValue
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.budget.addBudget.component.BudgetCategoryHeader
import com.jie.wealthmate.feature.budget.addBudget.component.BudgetCategoryItem
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.vo.CategoryVo
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_push_pin


class AddBudgetScreen() : BaseScreen() {
    @Composable
    override fun Content() {
        super.Content()

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: AddBudgetScreenModel = koinScreenModel()
        val uiState by screenModel.container.uiState.collectAsState()

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
        ) {
            WMTopBar(
                title = TopBarItem.Title("예산 추가"),
                readingItem = TopBarItem.ReadingItem().copy(action = { onBack() }),
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item {
                    WMCheckBox(
                        label = "카테고리 상세 태그 포함",
                        checked = uiState.isCategoryTagInclude,
                        onCheckedChange = screenModel::updateIsCategoryTagInclude,
                        modifier = Modifier
                            .padding(horizontal = 28.dp)
                            .padding(top = 4.dp)
                    )
                }

                // 수입 섹션
                item {
                    BudgetCategoryHeader(
                        modifier = Modifier.padding(horizontal = 28.dp),
                        label = "수입"
                    )
                }

                items(
                    items = uiState.incomeCategoryItems,
                    key = { it.id }
                ) { item ->
                    BudgetCategoryItem(
                        modifier = Modifier.padding(start = 20.dp, end = 28.dp),
                        item = item,
                        textFieldValue = uiState.incomeCategoryTextFieldMap[item.id]
                            ?: TextFieldValue(),
                        isCategoryTagInclude = uiState.isCategoryTagInclude,
                        tagTextFieldMap = uiState.incomeCategoryTextFieldMap,
                        onValueChange = { id, value ->
                            screenModel.updateIncomeTextField(id, value.toIntegerTextFieldValue())
                        }
                    )
                }

                // 저축 섹션
                item {
                    BudgetCategoryHeader(
                        modifier = Modifier.padding(horizontal = 28.dp),
                        label = "저축"
                    )
                }

                items(
                    items = uiState.savingCategoryItems,
                    key = { it.id }
                ) { item ->
                    BudgetCategoryItem(
                        modifier = Modifier.padding(start = 20.dp, end = 28.dp),
                        item = item,
                        textFieldValue = uiState.savingCategoryTextFieldMap[item.id]
                            ?: TextFieldValue(),
                        isCategoryTagInclude = uiState.isCategoryTagInclude,
                        tagTextFieldMap = uiState.savingCategoryTextFieldMap,
                        onValueChange = { id, value ->
                            screenModel.updateSavingTextField(id, value.toIntegerTextFieldValue())
                        }
                    )
                }

                // 지출 섹션
                item {
                    BudgetCategoryHeader(
                        modifier = Modifier.padding(horizontal = 28.dp),
                        label = "지출"
                    )
                }

                items(
                    items = uiState.expensesCategoryItems,
                    key = { it.id }
                ) { item ->
                    BudgetCategoryItem(
                        modifier = Modifier.padding(start = 20.dp, end = 28.dp),
                        item = item,
                        textFieldValue = uiState.expensesCategoryTextFieldMap[item.id]
                            ?: TextFieldValue(),
                        isCategoryTagInclude = uiState.isCategoryTagInclude,
                        tagTextFieldMap = uiState.expensesCategoryTextFieldMap,
                        onValueChange = { id, value ->
                            screenModel.updateExpensesTextField(id, value.toIntegerTextFieldValue())
                        }
                    )
                }
            }

            // 저장 버튼
            WMFloatingButton(
                text = "저장",
                buttonSize = ButtonSize.LARGE,
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                enabled = uiState.isDataChanged,
                onClick = screenModel::saveBudget
            )
        }
    }
}
