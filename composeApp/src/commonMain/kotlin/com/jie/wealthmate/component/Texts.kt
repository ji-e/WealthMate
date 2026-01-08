package com.jie.wealthmate.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.jie.wealthmate.theme.ColorGray

@Composable
fun WMText(
    text: AnnotatedString,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = ColorGray.Gray_700,
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
        style = style,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        textDecoration = textDecoration,
        fontSize = style.fontSize,
        lineHeight = lineHeight
    )
}

@Composable
fun WMText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = ColorGray.Gray_700,
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
        style = style,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
        textDecoration = textDecoration,
        fontSize = style.fontSize,
        lineHeight = lineHeight
    )
}
