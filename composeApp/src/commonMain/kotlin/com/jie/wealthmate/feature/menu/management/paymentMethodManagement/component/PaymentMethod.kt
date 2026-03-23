package com.jie.wealthmate.feature.menu.management.paymentMethodManagement.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.reorderable.ReorderableItem
import com.jie.wealthmate.component.reorderable.ReorderableLazyListState
import com.jie.wealthmate.component.reorderable.detectReorderAfterLongPress
import com.jie.wealthmate.component.reorderable.rememberReorderableLazyListState
import com.jie.wealthmate.component.reorderable.reorderable
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorGroup
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import com.jie.wealthmate.theme.Shapes
import com.jie.wealthmate.theme.WMTheme
import com.jie.wealthmate.vo.PaymentMethodVo
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_drag_handle

@Suppress("SuspiciousIndentation")
@Composable
fun ColumnScope.PaymentMethod(
    modifier: Modifier = Modifier,
    listState: ReorderableLazyListState,
    paymentMethodItems: List<PaymentMethodVo>,
    isDragging: Boolean = false,
    onIsDraggingChange: (Boolean) -> Unit = {},
    onItemClick: (PaymentMethodVo) -> Unit = {},
) {
    val hapticFeedback = LocalHapticFeedback.current

    // 그룹별 배경색 맵핑 최적화
    val groupColorMap = remember(paymentMethodItems) {
        val colorList = ColorGroup.getColorList()
        paymentMethodItems
            .filter { !it.groupLabel.isNullOrBlank() }
            .distinctBy { it.groupLabel }
            .mapIndexed { index, item ->
                item.groupLabel to colorList[index % colorList.size].second
            }
            .toMap()
    }

    LazyColumn(
        state = listState.listState,
        modifier = modifier
            .weight(1f)
            .reorderable(listState),
        contentPadding = PaddingValues(bottom = Padding.BackgroundBottom)
    ) {
        item {
            Spacer(modifier = Modifier.height(Padding.SpacerXS))
        }

        items(
            items = paymentMethodItems,
            key = { it.id }
        ) { paymentMethod ->
            ReorderableItem(
                state = listState,
                key = paymentMethod.id,
            ) { dragging ->
                // 드래그 상태 변화 시 부수 효과 처리
                LaunchedEffect(dragging) {
                    if (dragging) {
                        onIsDraggingChange(true)
                    }
                }

                if (dragging) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }

                // Modifier 메모이제이션
                val itemModifier = remember(dragging, isDragging) {
                    if (dragging) {
                        Modifier
                            .padding(horizontal = Padding.ContainerHorizontal)
                            .dropShadow(
                                shape = Shapes.small,
                                shadow = Shadow(
                                    radius = 10.dp,
                                    spread = 10.dp,
                                    color = ColorPrimary.Primary_200,
                                    offset = DpOffset(x = 4.dp, 4.dp)
                                )
                            )
                            .clip(Shapes.small)
                    } else {
                        Modifier.clickable(enabled = isDragging.not()) { onItemClick(paymentMethod) }
                    }
                }

                PaymentMethodItem(
                    data = paymentMethod,
                    groupBackgroundColor = groupColorMap[paymentMethod.groupLabel]
                        ?: ColorGray.Gray_200,
                    modifier = itemModifier,
                    onDragHandle = Modifier.detectReorderAfterLongPress(listState),
                )
            }
        }
    }
}

@Composable
fun PaymentMethodItem(
    data: PaymentMethodVo,
    groupBackgroundColor: Color,
    modifier: Modifier = Modifier,
    onDragHandle: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(ColorGray.White)
            .padding(horizontal = Padding.BackgroundHorizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (data.groupLabel.isNullOrBlank().not()) {
            WMText(
                text = data.groupLabel,
                style = typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(end = Padding.SpacerXS)
                    .clip(CircleShape)
                    .background(groupBackgroundColor)
                    .padding(horizontal = Padding.SpacerXS, vertical = Padding.SpacerXXS)
            )
        }

        WMText(
            text = data.label,
            style = typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f)
                .padding(end = Padding.SpacerXS),
            maxLines = 1,
        )

        Icon(
            painter = painterResource(Res.drawable.ic_drag_handle),
            contentDescription = "이동",
            tint = ColorSetting.DisabledContent,
            modifier = onDragHandle.size(28.dp)
        )
    }
}

@Preview
@Composable
private fun PaymentMethodPreview() {
    val listState = rememberReorderableLazyListState(onMove = { _, _ -> })
    val mockItems = persistentListOf(
        PaymentMethodVo(id = "1", label = "현대카드 M3", groupId = "1", groupLabel = "신용카드", sort = 1),
        PaymentMethodVo(id = "2", label = "카카오뱅크 체크", groupId = "2", groupLabel = "체크카드", sort = 2),
        PaymentMethodVo(id = "3", label = "현금", groupId = null, groupLabel = null, sort = 3),
    )

    WMTheme {
        Column {
            PaymentMethod(
                listState = listState,
                paymentMethodItems = mockItems
            )
        }
    }
}
