package io.github.drlacheheb.mqtlin.domain.util

import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull

data class TimeSeriesPoint(
    val timestamp: Long,
    val value: Double,
    val rawPayload: String
)

object NumericPayloadExtractor {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val numberRegex = Regex("""[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?""")

    /**
     * Extracts all named numeric metrics from a payload string.
     * Handles:
     * 1. JSON objects/primitives (e.g. {"temp": 21.5, "sensor": {"humidity": 60}} -> {"temp": 21.5, "sensor.humidity": 60.0})
     * 2. Plain numbers (e.g. "21.5" -> {"value": 21.5})
     * 3. Strings with numbers and units (e.g. "21.5 °C" -> {"value": 21.5})
     */
    fun extractMetrics(payload: String): Map<String, Double> {
        val trimmed = payload.trim()
        if (trimmed.isEmpty()) return emptyMap()

        // 1. Try JSON parsing
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
            try {
                val element = json.parseToJsonElement(trimmed)
                val map = mutableMapOf<String, Double>()
                flattenJsonNumeric(element, "", map)
                if (map.isNotEmpty()) return map
            } catch (_: Exception) {
                // Fallthrough to scalar extraction
            }
        }

        // 2. Try direct scalar Double parse
        val directDouble = trimmed.toDoubleOrNull()
        if (directDouble != null && !directDouble.isNaN() && !directDouble.isInfinite()) {
            return mapOf("value" to directDouble)
        }

        // 3. Try number regex extraction (e.g. "21.5 °C", "1013.25hPa", "55%")
        val match = numberRegex.find(trimmed)
        if (match != null) {
            val matchedDouble = match.value.toDoubleOrNull()
            if (matchedDouble != null && !matchedDouble.isNaN() && !matchedDouble.isInfinite()) {
                return mapOf("value" to matchedDouble)
            }
        }

        return emptyMap()
    }

    private fun flattenJsonNumeric(element: JsonElement, prefix: String, outMap: MutableMap<String, Double>) {
        when (element) {
            is JsonObject -> {
                element.forEach { (key, child) ->
                    val newKey = if (prefix.isEmpty()) key else "$prefix.$key"
                    flattenJsonNumeric(child, newKey, outMap)
                }
            }
            is JsonPrimitive -> {
                element.doubleOrNull?.let { doubleVal ->
                    if (!doubleVal.isNaN() && !doubleVal.isInfinite()) {
                        val finalKey = if (prefix.isEmpty()) "value" else prefix
                        outMap[finalKey] = doubleVal
                    }
                }
            }
            else -> {
                // arrays ignored for numeric extraction
            }
        }
    }

    /**
     * Builds a chronological time series from the message history for a given metric key.
     */
    fun buildTimeSeries(
        history: List<MqttMessage>,
        selectedMetric: String
    ): List<TimeSeriesPoint> {
        if (history.isEmpty()) return emptyList()

        // History is stored most-recent first; reverse to get oldest-first chronological order
        return history.reversed().mapNotNull { message ->
            val metrics = extractMetrics(message.payloadString)
            val value = metrics[selectedMetric]
                ?: if (selectedMetric.isEmpty() || selectedMetric == "value") metrics.values.firstOrNull() else null

            if (value != null) {
                TimeSeriesPoint(
                    timestamp = message.timestamp,
                    value = value,
                    rawPayload = message.payloadString
                )
            } else {
                null
            }
        }
    }
}
