package com.jie.wealthmate.component.textField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

private class IntegerVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.trim().replace(Regex("\\D"), "")
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }
        if (!originalText.all { it.isDigit() }) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formattedText = formatWithCommas(originalText)

        return TransformedText(
            AnnotatedString(formattedText),
            IntegerOffsetMapping(originalText, formattedText)
        )
    }

    private fun formatWithCommas(number: String): String {
        if (number.isEmpty()) return ""

        val reversed = number.reversed()
        val withCommas = reversed.chunked(3).joinToString(",")
        return withCommas.reversed()
    }
}

class IntegerOffsetMapping(originalText: String, formattedText: String) : OffsetMapping {
    private val originalLength: Int = originalText.length
    private val indexes = findDigitIndexes(originalText, formattedText)

    private fun findDigitIndexes(firstString: String, secondString: String): List<Int> {
        val digitIndexes = mutableListOf<Int>()
        var currentIndex = 0
        for (digit in firstString) {
            val index = secondString.indexOf(digit, currentIndex)
            if (index != -1) {
                digitIndexes.add(index)
                currentIndex = index + 1
            } else {
                return emptyList()
            }
        }
        return digitIndexes
    }

    override fun originalToTransformed(offset: Int): Int {
        if (offset >= originalLength) {
            return (indexes.lastOrNull() ?: -1) + 1
        }
        return indexes[offset]
    }

    override fun transformedToOriginal(offset: Int): Int {
        return indexes.indexOfFirst { it >= offset }.takeIf { it != -1 } ?: originalLength
    }
}

@Composable
fun rememberIntegerVisualTransformation(): VisualTransformation =
    remember { IntegerVisualTransformation() }

/**
 * IntegerComma를 위한 확장 함수
 * onValueChange에서 사용
 */
fun TextFieldValue.toIntegerTextFieldValue(): TextFieldValue {
    val filteredText = text
        .trim()
        .replace(Regex("\\D"), "")
        .run {
            if (isEmpty()) {
                ""
            } else {
                toLongOrNull()?.toString() ?: ""
            }
        }
    val selectionIndex = selection.end.coerceAtMost(filteredText.length)
    val newSelection = TextRange(selectionIndex)

    return TextFieldValue(
        text = filteredText,
        selection = newSelection
    )
}