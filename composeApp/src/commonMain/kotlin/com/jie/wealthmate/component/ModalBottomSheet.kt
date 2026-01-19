package com.jie.wealthmate.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.jie.wealthmate.theme.ColorGray
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.ic_horizontal_rule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WMModalBottomSheet(
    modifier: Modifier = Modifier,
    title: String? = null,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier,
        containerColor = ColorGray.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        onDismissRequest = onDismissRequest,
        dragHandle = {
            ->
            Icon(
                painter = painterResource(Res.drawable.ic_horizontal_rule),
                contentDescription = null,
                tint = ColorGray.Gray_400,
                modifier = Modifier.size(40.dp),
            )
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            title?.run {
                WMText(
                    text = this,
                    style = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 4.dp, bottom = 12.dp)
                )
            }
            content()
        }
    }
}

@Composable
fun WMModalBottomSheet(
    isVisible: Boolean,
    title: String? = null,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeight = constraints.maxHeight.toFloat()
        val offsetY = remember { Animatable(screenHeight) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(isVisible) {
            if (isVisible) {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            } else {
                offsetY.animateTo(
                    targetValue = screenHeight,
                    animationSpec = tween(300)
                )
            }
        }

        if (isVisible || offsetY.value < screenHeight) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // 배경 딤처리
                val alpha by animateFloatAsState(
                    targetValue = if (isVisible) 0.5f else 0f,
                    animationSpec = tween(300),
                    label = "alpha"
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ColorGray.Gray_700.copy(alpha = alpha))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            // 애니메이션으로 닫기
                            coroutineScope.launch {
                                offsetY.animateTo(
                                    targetValue = screenHeight,
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                )
                                onDismissRequest()
                            }
                        }
                )

                // Bottom Sheet
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .offset { IntOffset(0, offsetY.value.toInt()) }
                        .pointerInput(Unit) {
                            this.detectVerticalDragGestures(
                                onDragEnd = {
                                    coroutineScope.launch {
                                        // 드래그가 끝났을 때
                                        if (offsetY.value > screenHeight * 0.3f) {
                                            // 30% 이상 내렸으면 애니메이션으로 닫기
                                            offsetY.animateTo(
                                                targetValue = screenHeight,
                                                animationSpec = tween(
                                                    300,
                                                    easing = FastOutSlowInEasing
                                                )
                                            )
                                            onDismissRequest()
                                        } else {
                                            // 아니면 원위치
                                            offsetY.animateTo(
                                                targetValue = 0f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            )
                                        }
                                    }
                                },
                                onVerticalDrag = { _, dragAmount ->
                                    coroutineScope.launch {
                                        // 아래로만 드래그 가능 (위로는 안됨)
                                        val newOffset =
                                            (offsetY.value + dragAmount).coerceAtLeast(0f)
                                        offsetY.snapTo(newOffset)
                                    }
                                }
                            )
                        }
                        .background(
                            color = ColorGray.White,
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { /* Bottom Sheet 영역 클릭 시 닫히지 않도록 */ }
                ) {


                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(bottom = calculateAdjustedToastPadding())
                    ) {
                        // 드래그 핸들
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    // 애니메이션으로 닫기
                                    coroutineScope.launch {
                                        offsetY.animateTo(
                                            targetValue = screenHeight,
                                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                                        )
                                        onDismissRequest()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_horizontal_rule),
                                contentDescription = null,
                                tint = ColorGray.Gray_400,
                                modifier = Modifier.size(40.dp),
                            )
                        }

                        // 콘텐츠
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            title?.run {
                                WMText(
                                    text = this,
                                    style = Typography().titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .padding(top = 4.dp, bottom = 12.dp)
                                )
                            }
                            content()
                        }
                    }
                }
            }
        }
    }
}
