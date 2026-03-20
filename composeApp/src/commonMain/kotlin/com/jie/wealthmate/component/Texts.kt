package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.theme.ColorSetting
import com.jie.wealthmate.theme.WMTheme

/**
 * WealthMate 공통 텍스트 컴포넌트 (AnnotatedString 버전)
 */
@Composable
fun WMText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = ColorSetting.Default,
    fontWeight: FontWeight? = null,
    fontSize: TextUnit? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
) {
    val lineHeight =
        if (style.lineHeight != TextUnit.Unspecified) style.lineHeight
        else TextUnit.Unspecified

    Text(
        text = text,
        modifier = modifier,
        style = style.copy(
            color = color,
            fontWeight = fontWeight ?: style.fontWeight,
            fontSize = fontSize ?: style.fontSize,
            lineHeight = lineHeight,
        ),
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        textDecoration = textDecoration,
    )
}

/**
 * WealthMate 공통 텍스트 컴포넌트 (String 버전)
 */
@Composable
fun WMText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = ColorSetting.Default,
    fontWeight: FontWeight? = null,
    fontSize: TextUnit? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
    autoSize: TextAutoSize? = null,
) {
    val lineHeight =
        if (style.lineHeight != TextUnit.Unspecified) style.lineHeight
        else TextUnit.Unspecified

    Text(
        text = text,
        modifier = modifier,
        style = style.copy(
            color = color,
            fontWeight = fontWeight ?: style.fontWeight,
            fontSize = fontSize ?: style.fontSize,
            lineHeight = lineHeight,
        ),
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        textDecoration = textDecoration,
        autoSize = autoSize,
    )
}

/**
 * 화면의 주요 타이틀이나 헤더에 사용되는 텍스트
 */
@Composable
fun HeadLineText(
    text: String,
    modifier: Modifier = Modifier,
) {
    WMText(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(lineHeight = 32.sp),
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

/**
 * 보조 설명이나 정보성 텍스트에 사용
 */
@Composable
fun InfoText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
) {
    WMText(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = ColorSetting.Info,
        modifier = modifier,
        maxLines = maxLines
    )
}

/**
 * 입력 필드 등의 레이블 텍스트. 필수 여부 표시 가능.
 */
@Composable
fun LabelText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = ColorSetting.Default,
    isRequire: Boolean = false,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WMText(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
        )

        if (isRequire) {
            WMText(
                text = "*",
                style = MaterialTheme.typography.titleSmall,
                color = ColorSetting.Error,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Preview
@Composable
private fun TextsPreview() {
    WMTheme {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WMText(text = "Typography Variants", fontWeight = FontWeight.Bold)
                HeadLineText(text = "HeadLineText (20sp, SemiBold)")
                LabelText(text = "LabelText (Required)", isRequire = true)
                LabelText(text = "LabelText (Optional)", isRequire = false)
                InfoText(text = "InfoText: 보조 설명 등에 사용되는 회색 텍스트입니다.")
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WMText(text = "Customization", fontWeight = FontWeight.Bold)
                WMText(
                    text = "Custom Color & Size",
                    color = ColorSetting.Error,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                WMText(
                    text = "Text with Decoration",
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}
