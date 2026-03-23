package com.jie.wealthmate.feature.menu.management.categoryManagement.editCategory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.component.EmojiIcon
import com.jie.wealthmate.component.EmojiIconSize
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.Padding
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_change_circle
import wealthmate.composeapp.generated.resources.ic_check_circle

@Composable
fun CategoryIcon(
    modifier: Modifier = Modifier,
    largeCategory: LargeCategoryEnum,
    selectedCategoryIcon: CategoryIconEnum = CategoryIconEnum.defaultCategoryIcon,
    onClickChange: () -> Unit,
) {
    Box(modifier = modifier) {
        EmojiIcon(
            icon = selectedCategoryIcon.text,
            color = largeCategory.backgroundColor,
            size = EmojiIconSize.LARGE,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onClickChange() }
        )

        Box(
            modifier = Modifier
                .padding(2.dp)
                .clip(CircleShape)
                .background(color = ColorGray.White)
                .border(
                    width = 2.dp,
                    color = ColorGray.White_80,
                    shape = CircleShape
                )
                .size(30.dp)
                .align(Alignment.BottomEnd),
            contentAlignment = Alignment.Center
        ) {
            WMIconButton(
                iconModifier = Modifier.size(30.dp),
                iconRes = Res.drawable.ic_change_circle,
                tint = ColorGray.Gray_100,
                onClick = onClickChange,
            )
        }
    }
}

@Composable
fun CategoryIconGrid(
    modifier: Modifier = Modifier,
    selectedCategoryIcon: CategoryIconEnum,
    onIconChange: (CategoryIconEnum) -> Unit = {},
) {
    val iconItems = CategoryIconEnum.categoryIcons
    val pagerState = rememberPagerState(pageCount = { iconItems.size })
    val coroutineScope = rememberCoroutineScope()

    PrimaryScrollableTabRow(
        modifier = modifier.fillMaxWidth(),
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 20.dp,
        minTabWidth = 72.dp,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                Modifier.tabIndicatorOffset(pagerState.currentPage),
                width = Dp.Unspecified,
            )
        },
        containerColor = ColorGray.White,
        contentColor = ColorSetting.Default
    ) {
        iconItems.forEachIndexed { index, icon ->
            Tab(
                text = {
                    WMText(
                        text = icon.first.text,
                        fontSize = 36.sp
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
    HorizontalPager(pagerState) { page ->
        val selectedIconItems = iconItems[page].second

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier
                .background(color = ColorGray.White)
                .height(300.dp),
            verticalArrangement = Arrangement.spacedBy(Padding.SpacerXS),
            horizontalArrangement = Arrangement.spacedBy(Padding.SpacerXXS),
            contentPadding = PaddingValues(Padding.BackgroundHorizontal),
        ) {
            items(
                count = selectedIconItems.size
            ) { index ->
                val icon = selectedIconItems[index]
                Box(
                    modifier = Modifier.clickable { onIconChange(icon) },
                    contentAlignment = Alignment.Center
                ) {
                    WMText(
                        text = icon.text,
                        fontSize = 44.sp
                    )

                    if (selectedCategoryIcon.text == icon.text) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ColorGray.White)
                                .size(24.dp)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_check_circle),
                                contentDescription = null,
                                tint = ColorSetting.Primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
