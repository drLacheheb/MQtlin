package io.github.drlacheheb.mqtlin.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.TextFieldValue
import io.github.drlacheheb.mqtlin.ui.util.JsonSyntaxColors
import io.github.drlacheheb.mqtlin.ui.util.JsonVisualTransformation
import io.github.drlacheheb.mqtlin.ui.util.highlightJson
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class JsonHighlighterTest {

    @Test
    fun `highlightJson correctly distinguishes keys strings numbers and booleans`() {
        val json = """{"status": "online", "battery": 98, "active": true, "extra": null}"""
        val annotated: AnnotatedString = highlightJson(json)

        // Raw text preservation check
        annotated.text shouldBe json

        // Verify span styles are present
        val spanStyles = annotated.spanStyles
        spanStyles.isNotEmpty() shouldBe true

        // Find key span for "status"
        val statusKeySpan = spanStyles.find { it.start == json.indexOf("\"status\"") }
        statusKeySpan?.item?.color shouldBe JsonSyntaxColors.Key

        // Find string span for "online"
        val onlineValueSpan = spanStyles.find { it.start == json.indexOf("\"online\"") }
        onlineValueSpan?.item?.color shouldBe JsonSyntaxColors.StringVal

        // Find number span for 98
        val numberSpan = spanStyles.find { it.start == json.indexOf("98") }
        numberSpan?.item?.color shouldBe JsonSyntaxColors.NumberVal

        // Find boolean span for true
        val boolSpan = spanStyles.find { it.start == json.indexOf("true") }
        boolSpan?.item?.color shouldBe JsonSyntaxColors.BooleanOrNull

        // Find null span for null
        val nullSpan = spanStyles.find { it.start == json.indexOf("null") }
        nullSpan?.item?.color shouldBe JsonSyntaxColors.BooleanOrNull
    }

    @Test
    fun `JsonVisualTransformation preserves text 1 to 1 for cursor alignment`() {
        val transformation = JsonVisualTransformation()
        val input = """{"temp": -12.5}"""
        val transformed = transformation.filter(AnnotatedString(input))

        transformed.text.text shouldBe input
        transformed.offsetMapping.originalToTransformed(0) shouldBe 0
        transformed.offsetMapping.originalToTransformed(5) shouldBe 5
        transformed.offsetMapping.transformedToOriginal(5) shouldBe 5
    }
}
