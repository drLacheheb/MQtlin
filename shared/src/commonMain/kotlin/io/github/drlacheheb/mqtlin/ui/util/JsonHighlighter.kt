package io.github.drlacheheb.mqtlin.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiary

object JsonSyntaxColors {
    val Key: Color = MqtlinPrimary                // #C0C1FF (Lavender/Blue)
    val StringVal: Color = MqtlinSecondary        // #4EDEA3 (Emerald Green)
    val NumberVal: Color = MqtlinTertiary        // #FFB95F (Amber/Gold)
    val BooleanOrNull: Color = Color(0xFFFF9E64)  // #FF9E64 (Orange/Coral)
    val Punctuation: Color = DarkOutline          // #908FA0 (Muted Outline)
}

/**
 * Parses JSON text into a styled AnnotatedString with syntax highlighting
 * for keys, strings, numbers, booleans, null, and punctuation brackets.
 */
fun highlightJson(text: String): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        val len = text.length

        while (cursor < len) {
            val ch = text[cursor]

            if (ch == '"') {
                val start = cursor
                cursor++
                while (cursor < len && text[cursor] != '"') {
                    if (text[cursor] == '\\' && cursor + 1 < len) {
                        cursor++ // escape next char
                    }
                    cursor++
                }
                if (cursor < len) cursor++ // consume closing quote
                val strVal = text.substring(start, cursor)

                // Check if this string is a JSON object key (followed by colon)
                var lookAhead = cursor
                while (lookAhead < len && text[lookAhead].isWhitespace()) lookAhead++
                val isKey = lookAhead < len && text[lookAhead] == ':'

                withStyle(SpanStyle(color = if (isKey) JsonSyntaxColors.Key else JsonSyntaxColors.StringVal)) {
                    append(strVal)
                }
            } else if (ch in "{}[],:") {
                withStyle(SpanStyle(color = JsonSyntaxColors.Punctuation)) {
                    append(ch)
                }
                cursor++
            } else if (ch.isDigit() || ch == '-') {
                val start = cursor
                cursor++
                while (cursor < len && (text[cursor].isDigit() || text[cursor] in ".eE+-")) {
                    cursor++
                }
                val numVal = text.substring(start, cursor)
                withStyle(SpanStyle(color = JsonSyntaxColors.NumberVal)) {
                    append(numVal)
                }
            } else if (text.startsWith("true", cursor)) {
                withStyle(SpanStyle(color = JsonSyntaxColors.BooleanOrNull)) {
                    append("true")
                }
                cursor += 4
            } else if (text.startsWith("false", cursor)) {
                withStyle(SpanStyle(color = JsonSyntaxColors.BooleanOrNull)) {
                    append("false")
                }
                cursor += 5
            } else if (text.startsWith("null", cursor)) {
                withStyle(SpanStyle(color = JsonSyntaxColors.BooleanOrNull)) {
                    append("null")
                }
                cursor += 4
            } else {
                append(ch)
                cursor++
            }
        }
    }
}

/**
 * A Compose VisualTransformation that applies real-time JSON syntax highlighting
 * to editable BasicTextFields without modifying underlying string content.
 */
class JsonVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = highlightJson(text.text),
            offsetMapping = OffsetMapping.Identity
        )
    }
}

