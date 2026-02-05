package com.jie.wealthmate.feature.menu.component

import org.jetbrains.compose.resources.DrawableResource

enum class MenuEnum(
    val route: String,
    val label: String,
    val title: String,
    val icon: DrawableResource?,
) {
    CATEGORY(
        route = "category",
        label = "카테고리",
        title = "카테고리 관리",
        icon = null,
    ),
    PAYMENT_METHOD(
        route = "payment_method",
        label = "결제수단",
        title = "결제수단 관리",
        icon = null
    ),
    ASSET(
        route = "asset",
        label = "자산",
        title = "자산 관리",
        icon = null
    ),
    REPEAT_HISTORY(
        route = "repeat_history",
        label = "반복내역",
        title = "반복내역 관리",
        icon = null
    ),

    GOOGLE_SYNC(
        route = "google_sync",
        label = "백업 및 복구",
        title = "백업 및 복구",
        icon = null
    ),
    GOOGLE_SHARE(
        route = "share",
        label = "공유",
        title = "공유",
        icon = null
    )

    ;

    companion object {
        val managementMenu = listOf(
            CATEGORY,
            PAYMENT_METHOD,
            ASSET,
//            REPEAT_HISTORY,
        )
        val syncMenu = listOf(
            GOOGLE_SYNC,
            GOOGLE_SHARE
        )

    }
}