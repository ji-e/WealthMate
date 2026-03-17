package com.jie.wealthmate.feature.home.preparednessStatus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jie.wealthmate.component.WMListSelectionModalBottomSheet
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.topbar.TopBarItem
import com.jie.wealthmate.component.topbar.WMTopBar
import com.jie.wealthmate.feature.calendar.component.listCalendar.DateHeader
import com.jie.wealthmate.feature.calendar.component.listCalendar.HistoryItem
import com.jie.wealthmate.feature.home.StatusType
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.noRippleClickable
import com.jie.wealthmate.utils.formatWithCommas
import com.jie.wealthmate.vo.HistoryVo.Companion.mapperToVo
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_arrow_drop_down
import kotlin.math.absoluteValue

class PreparednessStatusScreen(
    private val initialStatusType: StatusType,
    private val initialLargeCategory: LargeCategoryEnum,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: PreparednessStatusScreenModel = koinScreenModel {
            parametersOf(initialStatusType, initialLargeCategory)
        }
        val uiState by screenModel.container.uiState.collectAsState()

        var isShowStatusTypeModal by remember { mutableStateOf(false) }
        var isShowLargeCategoryModal by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize()) {
            WMTopBar(
                title = TopBarItem.Title("대비 현황"),
                readingItem = TopBarItem.ReadingItem(
                    action = { navigator.pop() }
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorGray.White)
            ) {
                // 필터 섹션
                Row(
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterItem(
                        text = uiState.statusType.label,
                        onClick = { isShowStatusTypeModal = true }
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    FilterItem(
                        text = uiState.largeCategory.label,
                        onClick = { isShowLargeCategoryModal = true }
                    )
                }

                // 요약 섹션
                SummarySection(uiState)

                HorizontalDivider(
                    modifier = Modifier.padding(top = 24.dp),
                    color = ColorGray.Gray_50,
                    thickness = 12.dp
                )

                // 리스트 섹션
                val groupedHistories = remember(uiState.histories) {
                    uiState.histories.groupBy { it.history.date }
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    groupedHistories.forEach { (dateMillis, histories) ->
                        item {
                            DateHeader(histories.first().mapperToVo().date)
                        }
                        items(histories) { history ->
                            HistoryItem(
                                history = history.mapperToVo(),
                                onItemClick = { /* 상세 이동 필요 시 구현 */ }
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }

        if (isShowStatusTypeModal) {
            WMListSelectionModalBottomSheet(
                title = "기간 선택",
                items = StatusType.entries,
                selectedItem = uiState.statusType,
                itemLabel = { it.label },
                onItemSelected = { screenModel.updateStatusType(it) },
                onDismissRequest = { isShowStatusTypeModal = false }
            )
        }

        if (isShowLargeCategoryModal) {
            WMListSelectionModalBottomSheet(
                title = "카테고리 선택",
                items = LargeCategoryEnum.entries,
                selectedItem = uiState.largeCategory,
                itemLabel = { it.label },
                onItemSelected = { screenModel.updateLargeCategory(it) },
                onDismissRequest = { isShowLargeCategoryModal = false }
            )
        }
    }

    @Composable
    private fun FilterItem(text: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier.noRippleClickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WMText(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_drop_down),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    @Composable
    private fun SummarySection(uiState: PreparednessStatusUiState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 8.dp)
        ) {
            WMText(
                text = "${uiState.statusType.label} 총 ${uiState.largeCategory.label}",
                style = MaterialTheme.typography.bodyMedium.copy(color = ColorGray.Gray_500)
            )
            WMText(
                text = "${formatWithCommas(uiState.currentAmount.toString())}원",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            val diffText = if (uiState.diffAmount >= 0) "더" else "덜"
            val color =
                if (uiState.diffAmount >= 0) ColorPrimary.Primary_600 else ColorGray.Gray_500

            Row(verticalAlignment = Alignment.CenterVertically) {
                WMText(
                    text = "${uiState.statusType.lastLabel} 대비 ",
                    style = MaterialTheme.typography.bodyMedium.copy(color = ColorGray.Gray_500)
                )
                WMText(
                    text = "${formatWithCommas(uiState.diffAmount.absoluteValue.toString())}원 ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                )
                WMText(
                    text = "${diffText} 썼어요",
                    style = MaterialTheme.typography.bodyMedium.copy(color = ColorGray.Gray_500)
                )

                uiState.diffPercentage?.let {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(ColorGray.Gray_50, shape = MaterialTheme.shapes.small)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        WMText(
                            text = "${if (it >= 0) "+" else ""}$it%",
                            style = MaterialTheme.typography.labelSmall.copy(color = ColorGray.Gray_600)
                        )
                    }
                }
            }
        }
    }
}
