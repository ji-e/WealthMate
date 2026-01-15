package com.jie.wealthmate.feature.menu.categorySetting.addCategory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.component.CategoryIconEnum
import com.jie.wealthmate.component.WMIconButton
import com.jie.wealthmate.component.WMText
import com.jie.wealthmate.feature.menu.categorySetting.component.LargeCategoryEnum
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorPrimary
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
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(color = largeCategory.backgroundColor)
                .size(100.dp)
                .clickable { onClickChange() },
            contentAlignment = Alignment.Center
        ) {

            WMText(
                text = selectedCategoryIcon.text,
                style = Typography().bodyLarge.copy(fontSize = 76.sp)
            )
        }
        Box(
            modifier = Modifier
                .padding(2.dp)
                .clip(CircleShape)
                .background(color = ColorGray.White)
                .border(
                    width = 2.dp,
                    color = ColorGray.White_80,
                )
                .size(30.dp)
                .align(Alignment.BottomEnd),
            contentAlignment = Alignment.Center
        ) {
            Spacer(
                modifier = Modifier
                    .background(
                        color = ColorGray.Gray_700,
                        shape = CircleShape
                    )
                    .size(24.dp)
            )
            WMIconButton(
                modifier = Modifier.size(30.dp),
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
        contentColor = ColorGray.Gray_700
    ) {
        iconItems.forEachIndexed { index, icon ->
            Tab(
                text = {
                    WMText(
                        text = icon.first.text,
                        style = Typography().bodyLarge.copy(fontSize = 36.sp)
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
    HorizontalPager(pagerState) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = modifier
                .background(color = ColorGray.White)
                .height(300.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(20.dp),
        ) {
            val selectedIconItems = iconItems[pagerState.currentPage].second
            items(
                count = selectedIconItems.size
            ) { index ->
                val icon = selectedIconItems[index]
                Box(contentAlignment = Alignment.Center) {
                    WMText(
                        text = icon.text,
                        style = Typography().bodyLarge.copy(fontSize = 44.sp)
                    )

                    if (selectedCategoryIcon.text == icon.text) {
                        Spacer(
                            modifier = modifier
                                .clip(CircleShape)
                                .background(ColorGray.White)
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                        )

                        Icon(
                            painter = painterResource(Res.drawable.ic_check_circle),
                            contentDescription = null,
                            tint = ColorPrimary.Primary_700,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }
            }
        }
    }
}
