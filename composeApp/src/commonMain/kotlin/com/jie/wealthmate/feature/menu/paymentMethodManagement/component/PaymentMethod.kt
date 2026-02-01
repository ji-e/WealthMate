package com.jie.wealthmate.feature.menu.paymentMethodManagement.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.component.reorderable.ReorderableItem
import com.jie.wealthmate.component.reorderable.ReorderableLazyListState
import com.jie.wealthmate.component.reorderable.detectReorderAfterLongPress
import com.jie.wealthmate.component.reorderable.reorderable
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorGroup
import com.jie.wealthmate.theme.ColorPrimary
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_drag_handle

@Suppress("SuspiciousIndentation")
@Composable
fun ColumnScope.PaymentMethod(
    modifier: Modifier = Modifier,
    listState: ReorderableLazyListState,
    paymentMethodItems: List<PaymentMethodItemData>,
    isDragging: Boolean = false,
    onIsDraggingChange: (Boolean) -> Unit = {},
    onItemClick: (PaymentMethodItemData) -> Unit = {},
) {
    val hapticFeedback = LocalHapticFeedback.current
    val colorList = ColorGroup.getColorList()
    val groupColorMap = paymentMethodItems
        .distinctBy { it.groupLabel }
        .mapIndexed { index, item ->
            item.groupLabel to colorList[index % colorList.size].second
        }
        .toMap()

        LazyColumn(
            state = listState.listState,
            modifier = modifier
                .weight(1f)
                .reorderable(listState),
        ) {
            items(
                count = paymentMethodItems.size,
                key = { index -> paymentMethodItems[index].id }
            ) { index ->
                val paymentMethod = paymentMethodItems[index]
                ReorderableItem(
                    state = listState,
                    key = paymentMethod.id,
                ) {
                    if (isDragging.not()) {
                        onIsDraggingChange(it)
                    }

                    if (it) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }

                    PaymentMethodItem(
                        data = paymentMethod,
                        groupBackgroundColor = groupColorMap[paymentMethod.groupLabel] ?: ColorGray.Gray_200,
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
                                    onItemClick(paymentMethod)
                                }
                            }
                        ),
                        onDragHandle = Modifier.detectReorderAfterLongPress(listState),
                    )
                }
            }
        }
}

@Composable
fun PaymentMethodItem(
    data: PaymentMethodItemData,
    groupBackgroundColor: Color,
    modifier: Modifier = Modifier,
    onDragHandle: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorGray.White)
            .padding(
                vertical = 14.dp,
                horizontal = 20.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        if (data.groupLabel != null) {
            WMText(
                text = data.groupLabel,
                style = Typography().labelMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier
                    .padding(end = 6.dp)
                    .clip(CircleShape)
                    .background(groupBackgroundColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        WMText(
            text = data.label,
            style = Typography().bodyLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            maxLines = 1,
        )

        Icon(
            painter = painterResource(Res.drawable.ic_drag_handle),
            contentDescription = "이동",
            tint = ColorGray.Gray_300,
            modifier = onDragHandle.size(28.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PaymentMethodItemPreview() {
    WMTheme {
        PaymentMethodItem(
            PaymentMethodItemData(
                id = "1",
                label = "카드",
                groupId = "1",
                groupLabel = "신용카드",
                sort = 0
            ),
            groupBackgroundColor = ColorGray.Gray_200
        )
    }
}