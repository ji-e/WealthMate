package com.jie.wealthmate.feature.menu.categoryManagement.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray

@Composable
fun CategoryTap(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    onTapClick: (Int) -> Unit = {},
) {
    val largeCategoryItems = listOf(
        LargeCategoryEnum.INCOME,
        LargeCategoryEnum.EXPENSES,
        LargeCategoryEnum.SAVING,
    )

    PrimaryTabRow(
        modifier = modifier.fillMaxWidth(),
        selectedTabIndex = pagerState.currentPage,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                Modifier.tabIndicatorOffset(pagerState.currentPage),
                width = Dp.Unspecified,
            )
        },
        containerColor = ColorGray.White,
        contentColor = ColorGray.Gray_700
    ) {
        largeCategoryItems.forEachIndexed { index, largeCategory ->
            Tab(
                text = {
                    WMText(
                        text = largeCategory.label,
                        style = Typography().titleMedium
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = { onTapClick(index) }
            )
        }
    }
}