package com.jie.wealthmate.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jie.wealthmate.theme.ColorGray
import com.jie.wealthmate.theme.ColorRed
import com.jie.wealthmate.theme.wantedSansFontFamily

@Composable
fun WMText(
    text: AnnotatedString,
    style: TextStyle = Typography().bodyMedium,
    modifier: Modifier = Modifier,
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
        style = style.copy(lineHeight = lineHeight),
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        textDecoration = textDecoration,
    )
}

@Composable
fun WMText(
    text: String,
    style: TextStyle = Typography().bodyMedium,
    modifier: Modifier = Modifier,
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
        style = style.copy(lineHeight = lineHeight),
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        textDecoration = textDecoration,
        fontFamily = wantedSansFontFamily(),
        autoSize = autoSize
    )
}


@Composable
fun HeadLineText(
    modifier: Modifier = Modifier,
    text: String,
) {
    WMText(
        text = text,
        style = Typography().titleMedium.copy(
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 32.sp
        ),
        modifier = modifier
    )
}

@Composable
fun InfoText(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int = Int.MAX_VALUE,
) {
    WMText(
        text = text,
        style = Typography().bodyMedium.copy(color = ColorGray.Gray_500),
        modifier = modifier,
        maxLines = maxLines
    )
}

@Composable
fun LabelText(
    modifier: Modifier = Modifier,
    text: String,
    isRequire: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        WMText(
            text = text,
            style = Typography().titleSmall.copy(fontWeight = FontWeight.SemiBold),
        )

        if (isRequire) {
            WMText(
                text = "*",
                style = Typography().titleSmall.copy(
                    color = ColorRed.Red_300,
                    fontWeight = FontWeight.SemiBold,
                )
            )
        }
    }
}
