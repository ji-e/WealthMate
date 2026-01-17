package com.jie.wealthmate.component.reorderable

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope


@Composable
fun rememberReorderableLazyGridState(
    onMove: (ItemPosition, ItemPosition) -> Unit,
    gridState: LazyGridState = rememberLazyGridState(),
    canDragOver: ((draggedOver: ItemPosition, dragging: ItemPosition) -> Boolean)? = null,
    onDragEnd: ((startIndex: Int, endIndex: Int) -> Unit)? = null,
    maxScrollPerFrame: Dp = 20.dp,
    dragCancelledAnimation: DragCancelledAnimation = SpringDragCancelledAnimation(),
): ReorderableLazyGridState {
    val maxScroll = with(LocalDensity.current) { maxScrollPerFrame.toPx() }
    val scope = rememberCoroutineScope()
    val state = remember(gridState) {
        ReorderableLazyGridState(
            gridState = gridState,
            scope = scope,
            maxScrollPerFrame = maxScroll,
            onMove = onMove,
            canDragOver = canDragOver,
            onDragEnd = onDragEnd,
            dragCancelledAnimation = dragCancelledAnimation
        )
    }

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    LaunchedEffect(state) {
        state.visibleItemsChanged().collect { state.onDrag(0, 0) }
    }

    LaunchedEffect(state) {
        var reverseDirection = !gridState.layoutInfo.reverseLayout
        if (isRtl) {
            reverseDirection = !reverseDirection
        }

        val direction = if (reverseDirection) 1f else -1f
        while (true) {
            val diff = state.scrollChannel.receive()
            gridState.scrollBy(diff * direction)
        }
    }

    return state
}


class ReorderableLazyGridState(
    val gridState: LazyGridState,
    scope: CoroutineScope,
    maxScrollPerFrame: Float,
    onMove: (fromIndex: ItemPosition, toIndex: ItemPosition) -> Unit,
    canDragOver: ((draggedOver: ItemPosition, dragging: ItemPosition) -> Boolean)? = null,
    onDragEnd: ((startIndex: Int, endIndex: Int) -> Unit)? = null,
    dragCancelledAnimation: DragCancelledAnimation = SpringDragCancelledAnimation(),
) : ReorderableState<LazyGridItemInfo>(
    scope = scope,
    maxScrollPerFrame = maxScrollPerFrame,
    onMove = onMove,
    canDragOver = canDragOver,
    onDragEnd = onDragEnd,
    dragCancelledAnimation = dragCancelledAnimation
) {
    override val isVerticalScroll: Boolean
        get() = gridState.layoutInfo.orientation == Orientation.Vertical

    override val LazyGridItemInfo.left: Int
        get() = offset.x

    override val LazyGridItemInfo.top: Int
        get() = offset.y

    override val LazyGridItemInfo.right: Int
        get() = offset.x + size.width

    override val LazyGridItemInfo.bottom: Int
        get() = offset.y + size.height

    override val LazyGridItemInfo.width: Int
        get() = size.width

    override val LazyGridItemInfo.height: Int
        get() = size.height

    override val LazyGridItemInfo.itemIndex: Int
        get() = index

    override val LazyGridItemInfo.itemKey: Any
        get() = key

    override val visibleItemsInfo: List<LazyGridItemInfo>
        get() = gridState.layoutInfo.visibleItemsInfo

    override val viewportStartOffset: Int
        get() = 0 // Grid는 보통 0 기준

    override val viewportEndOffset: Int
        get() = if (isVerticalScroll) gridState.layoutInfo.viewportSize.height
        else gridState.layoutInfo.viewportSize.width

    override val firstVisibleItemIndex: Int
        get() = gridState.firstVisibleItemIndex

    override val firstVisibleItemScrollOffset: Int
        get() = gridState.firstVisibleItemScrollOffset

    override suspend fun scrollToItem(index: Int, offset: Int) {
        gridState.scrollToItem(index, offset)
    }

    override fun findTargets(x: Int, y: Int, selected: LazyGridItemInfo) =
        super.findTargets(x, y, selected)

    override fun chooseDropItem(
        draggedItemInfo: LazyGridItemInfo?,
        items: List<LazyGridItemInfo>,
        curX: Int,
        curY: Int,
    ) = super.chooseDropItem(draggedItemInfo, items, curX, curY)
}