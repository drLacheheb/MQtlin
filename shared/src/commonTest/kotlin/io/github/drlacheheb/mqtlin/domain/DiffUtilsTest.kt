package io.github.drlacheheb.mqtlin.domain

import io.github.drlacheheb.mqtlin.domain.util.DiffType
import io.github.drlacheheb.mqtlin.domain.util.DiffUtils
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DiffUtilsTest {
    @Test
    fun `identical strings produce diff with no additions or deletions`() {
        val text = """{"status": "online", "value": 42}"""
        val result = DiffUtils.computeDiff(oldText = text, newText = text)

        result.hasChanges shouldBe false
        result.additions shouldBe 0
        result.deletions shouldBe 0
        result.lines.all { it.type == DiffType.UNCHANGED } shouldBe true
    }

    @Test
    fun `diff identifies added and deleted lines in JSON structures`() {
        val oldJson =
            """
            {
              "status": "idle",
              "battery": 100
            }
            """.trimIndent()

        val newJson =
            """
            {
              "status": "active",
              "battery": 100,
              "temperature": 25.4
            }
            """.trimIndent()

        val result = DiffUtils.computeDiff(oldText = oldJson, newText = newJson)

        result.hasChanges shouldBe true
        result.additions shouldBe 3 // "status": "active", "battery": 100,, "temperature": 25.4
        result.deletions shouldBe 2 // "status": "idle", "battery": 100

        val addedLines = result.lines.filter { it.type == DiffType.ADDED }
        addedLines.any { it.text.contains("active") } shouldBe true
        addedLines.any { it.text.contains("temperature") } shouldBe true

        val deletedLines = result.lines.filter { it.type == DiffType.DELETED }
        deletedLines.any { it.text.contains("idle") } shouldBe true
    }

    @Test
    fun `diffing from empty string marks all new lines as added`() {
        val newText = "line1\nline2\nline3"
        val result = DiffUtils.computeDiff(oldText = "", newText = newText, prettifyJson = false)

        result.hasChanges shouldBe true
        result.additions shouldBe 3
        result.deletions shouldBe 0
        result.lines.all { it.type == DiffType.ADDED } shouldBe true
    }

    @Test
    fun `diffing to empty string marks all old lines as deleted`() {
        val oldText = "line1\nline2"
        val result = DiffUtils.computeDiff(oldText = oldText, newText = "", prettifyJson = false)

        result.hasChanges shouldBe true
        result.additions shouldBe 0
        result.deletions shouldBe 2
        result.lines.all { it.type == DiffType.DELETED } shouldBe true
    }
}
