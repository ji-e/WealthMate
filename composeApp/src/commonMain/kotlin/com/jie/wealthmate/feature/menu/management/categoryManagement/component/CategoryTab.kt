package com.jie.wealthmate.feature.menu.management.categoryManagement.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorSetting

@Composable
fun CategoryTab(
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
        contentColor = ColorSetting.Default
    ) {
        largeCategoryItems.forEachIndexed { index, largeCategory ->
            Tab(
                text = {
                    WMText(
                        text = largeCategory.label,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = { onTapClick(index) }
            )
        }
    }
}