package io.github.drlacheheb.mqtlin.domain.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@OptIn(ExperimentalSerializationApi::class)
object JsonUtils {

    private val prettyJson = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
        isLenient = true
        ignoreUnknownKeys = true
    }

    /**
     * Validates if the given [raw] string is valid JSON.
     * Returns null if valid, or a descriptive error message if invalid.
     */
    fun validate(raw: String): String? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
            return "Expected JSON object '{' or array '['"
        }
        return try {
            prettyJson.parseToJsonElement(trimmed)
            null
        } catch (e: Exception) {
            e.message ?: "Invalid JSON syntax"
        }
    }

    /**
     * Formats and pretty-prints the given [raw] JSON string with 2-space indentation.
     * Returns the original string if parsing fails.
     */
    fun format(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return raw
        return try {
            val element = prettyJson.parseToJsonElement(trimmed)
            prettyJson.encodeToString(JsonElement.serializer(), element)
        } catch (_: Exception) {
            raw
        }
    }

    fun isValidJson(raw: String): Boolean = validate(raw) == null

    fun formatOrRaw(raw: String): String = format(raw)
}

