package com.jie.wealthmate.component.textField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatWithCommas

/**
 * 숫자를 3자리마다 콤마(,)로 포맷팅하는 VisualTransformation
 */
private class IntegerVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formattedText = originalText.formatWithCommas()

        return TransformedText(
            AnnotatedString(formattedText),
            IntegerOffsetMapping(originalText, formattedText)
        )
    }
}

private class IntegerOffsetMapping(
    private val originalText: String,
    private val formattedText: String
) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        if (offset <= 0) return 0
        val safeOffset = offset.coerceAtMost(originalText.length)
        
        var transformedOffset = 0
        var originalCount = 0
        for (char in formattedText) {
            if (originalCount == safeOffset) break
            transformedOffset++
            if (char.isDigit()) originalCount++
        }
        return transformedOffset
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (offset <= 0) return 0
        val safeOffset = offset.coerceAtMost(formattedText.length)
        var originalOffset = 0
        for (i in 0 until safeOffset) {
            if (formattedText[i].isDigit()) originalOffset++
        }
        return originalOffset
    }
}

@Composable
fun rememberIntegerVisualTransformation(): VisualTransformation =
    remember { IntegerVisualTransformation() }

/**
 * 숫지만 허용하고 콤마를 제거한 TextFieldValue로 변환 (onValueChange에서 사용)
 */
fun TextFieldValue.toIntegerTextFieldValue(): TextFieldValue {
    val filteredText = text.filter { it.isDigit() }
    
    // "0"으로 시작하는 경우 처리 (단, "0" 그 자체는 허용)
    val sanitizedText = if (filteredText.length > 1 && filteredText.startsWith("0")) {
        filteredText.toLongOrNull()?.toString().default()
    } else {
        filteredText
    }

    return copy(
        text = sanitizedText,
        selection = TextRange(sanitizedText.length)
    )
}
