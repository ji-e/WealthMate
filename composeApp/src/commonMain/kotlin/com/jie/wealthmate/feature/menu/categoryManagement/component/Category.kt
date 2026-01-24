package com.jie.wealthmate.feature.menu.categoryManagement.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.WMTheme
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_drag_handle

@Composable
fun CategoryItem(
    data: CategoryItemData,
    modifier: Modifier = Modifier,
    onDragHandle: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorGray.White)
            .padding(
                vertical = 10.dp,
                horizontal = 20.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(data.largeCategory.backgroundColor)
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            WMText(
                text = data.icon,
                style = Typography().bodyLarge.copy(fontSize = 28.sp)
            )
        }

        WMText(
            text = data.label,
            style = Typography().bodyLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f),
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
private fun CategoryItemPreview() {
    WMTheme {
        CategoryItem(
            CategoryItemData(
                id = "0",
                icon = CategoryIconEnum.CATEGORY_U1F9D0.text,
                label = "급여",
                sort = 1,
                largeCategory = LargeCategoryEnum.EXPENSES
            )
        )
    }
}