package io.github.drlacheheb.mqtlin.domain

import io.github.drlacheheb.mqtlin.domain.util.JsonUtils
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class JsonUtilsTest {
    @Test
    fun `valid JSON object passes validation`() {
        val json = """{"sensor": "temp", "value": 23.5, "active": true}"""
        val error = JsonUtils.validate(json)
        error.shouldBeNull()
    }

    @Test
    fun `valid JSON array passes validation`() {
        val json = """[1, 2, "three", {"nested": true}]"""
        val error = JsonUtils.validate(json)
        error.shouldBeNull()
    }

    @Test
    fun `empty string passes validation`() {
        JsonUtils.validate("").shouldBeNull()
        JsonUtils.validate("   ").shouldBeNull()
    }

    @Test
    fun `invalid non-json text returns syntax error`() {
        val error = JsonUtils.validate("just plain text")
        error.shouldNotBeNull()
        error shouldContain "Expected JSON object"
    }

    @Test
    fun `malformed JSON with missing comma returns syntax error`() {
        val malformed =
            """
            {
              "device": "D1"
              "status": "OK"
            }
            """.trimIndent()
        val error = JsonUtils.validate(malformed)
        error.shouldNotBeNull()
    }

    @Test
    fun `format beautifies unformatted JSON with indentation`() {
        val compact = """{"a":1,"b":["x","y"],"c":{"nested":true}}"""
        val formatted = JsonUtils.format(compact)

        formatted shouldContain "\n"
        formatted shouldContain "  \"a\": 1"
        formatted shouldContain "  \"b\": ["
    }

    @Test
    fun `format returns original string on malformed JSON`() {
        val invalid = "{ bad json "
        val formatted = JsonUtils.format(invalid)
        formatted shouldBe invalid
    }
}
