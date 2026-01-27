@file:OptIn(InternalVoyagerApi::class)

package com.jie.wealthmate.feature.calendar.addHistory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.jie.wealthmate.base.BaseScreen
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.textField.WMTextField
import com.jie.wealthmate.component.textField.rememberIntegerVisualTransformation
import com.jie.wealthmate.component.textField.toIntegerTextFieldValue
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.feature.calendar.addHistory.component.CategoryTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.DateTextField
import com.jie.wealthmate.feature.calendar.addHistory.component.LargeCategorySelectBox
import com.jie.wealthmate.feature.calendar.addHistory.component.PaymentMethodTextField
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryTagVo
import org.koin.compose.koinInject

class AddHistoryScreen() : BaseScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: AddHistoryScreenModel = koinInject()
        val uiState = screenModel.container.uiState.collectAsState().value


        fun onBack() {
            showSaveBackDialog(uiState.isChangedData) {
                navigator.pop()
            }
        }

        BackHandler(true) { onBack() }

        if (navigator.lastItem is AddHistoryScreen) {
            SideEffect {
                screenModel.updateTopBar(
                    title = TopBarItem.Title("내역 추가"),
                    readingItem = TopBarItem.ReadingItem().copy(
                        action = { onBack() }
                    ),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 수입, 지출, 저출 카테고리 선택
            LargeCategorySelectBox(
                modifier = Modifier.padding(top=8.dp)
            )

            // 날짜 선택
            DateTextField(
                modifier = Modifier.padding(top = 24.dp),
                date = today,
                installmentCount = 3,
                onDateClick = {},
                onRepeatClick = {},
                onInstallmentClick = {}
            )

            // 금액 입력
            WMTextField(
                modifier = Modifier.padding(top = 20.dp),
                value = uiState.amount,
                onValueChange = {
                    screenModel.updateAmount(it.toIntegerTextFieldValue())
                },
                label = "금액",
                isRequire = true,
                maxLength = 10,
                placeholder = "금액을 입력해 주세요.",
                suffix = {
                    WMText(
                        text = "원",
                        style = Typography().bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                },
                visualTransformation = rememberIntegerVisualTransformation(),
            )

            // 카테고리 선택
            CategoryTextField(
                tagLabelItems = listOf(
                    CategoryTagVo(
                        id = "0",
                        label = "외식"
                    ),
                    CategoryTagVo(
                        id = "1",
                        label = "배달"
                    )
                ),
                selectedTagLabel = CategoryTagVo(
                    id = "0",
                    label = "외식"
                )
            )

            // 결제수단/자산 선택
            PaymentMethodTextField(
                modifier = Modifier.padding(top = 24.dp),
            )

            // 내용 입력
            WMTextField(
                value = uiState.content,
                onValueChange = screenModel::updateContent,
                label = "내용",
                maxLength = 20,
                placeholder = "내용을 입력해 주세요.",
            )


        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AddHistoryScreenPreview() {
    WMTheme {
        AddHistoryScreen().Content()
    }
}