package io.github.drlacheheb.mqtlin.domain

import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.util.NumericPayloadExtractor
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NumericPayloadExtractorTest {
    @Test
    fun `extractMetrics parses raw scalar numbers`() {
        NumericPayloadExtractor.extractMetrics("42")["value"] shouldBe 42.0
        NumericPayloadExtractor.extractMetrics("-3.14")["value"]!! shouldBe (-3.14 plusOrMinus 0.001)
        NumericPayloadExtractor.extractMetrics("0.005")["value"]!! shouldBe (0.005 plusOrMinus 0.0001)
    }

    @Test
    fun `extractMetrics parses numbers with units or symbols`() {
        NumericPayloadExtractor.extractMetrics("21.5 °C")["value"]!! shouldBe (21.5 plusOrMinus 0.001)
        NumericPayloadExtractor.extractMetrics("55%")["value"]!! shouldBe (55.0 plusOrMinus 0.001)
        NumericPayloadExtractor.extractMetrics("3.3V")["value"]!! shouldBe (3.3 plusOrMinus 0.001)
        NumericPayloadExtractor.extractMetrics("1013.25 hPa")["value"]!! shouldBe (1013.25 plusOrMinus 0.001)
    }

    @Test
    fun `extractMetrics parses flat and nested JSON numbers`() {
        val flatJson = """{"temperature": 21.5, "humidity": 60, "status": "OK"}"""
        val flatMetrics = NumericPayloadExtractor.extractMetrics(flatJson)
        flatMetrics["temperature"]!! shouldBe (21.5 plusOrMinus 0.001)
        flatMetrics["humidity"]!! shouldBe (60.0 plusOrMinus 0.001)

        val nestedJson =
            """
            {
                "device_id": "ESP_01",
                "metrics": {
                    "voltage": 3.28,
                    "rssi": -65
                }
            }
            """.trimIndent()
        val nestedMetrics = NumericPayloadExtractor.extractMetrics(nestedJson)
        nestedMetrics["metrics.voltage"]!! shouldBe (3.28 plusOrMinus 0.001)
        nestedMetrics["metrics.rssi"]!! shouldBe (-65.0 plusOrMinus 0.001)
    }

    @Test
    fun `extractMetrics returns empty map for non-numeric strings`() {
        NumericPayloadExtractor.extractMetrics("").shouldBeEmpty()
        NumericPayloadExtractor.extractMetrics("   ").shouldBeEmpty()
        NumericPayloadExtractor.extractMetrics("HELLO_WORLD").shouldBeEmpty()
        NumericPayloadExtractor.extractMetrics("""{"status": "online", "mode": "idle"}""").shouldBeEmpty()
    }

    @Test
    fun `buildTimeSeries orders points chronologically and extracts chosen metric`() {
        val history =
            listOf(
                MqttMessage(topic = "sensor/temp", payload = "23.0".encodeToByteArray(), timestamp = 3000L),
                MqttMessage(topic = "sensor/temp", payload = "22.0".encodeToByteArray(), timestamp = 2000L),
                MqttMessage(topic = "sensor/temp", payload = "21.0".encodeToByteArray(), timestamp = 1000L),
            )

        val points = NumericPayloadExtractor.buildTimeSeries(history, "value")
        points shouldHaveSize 3
        points[0].timestamp shouldBe 1000L
        points[0].value shouldBe 21.0
        points[1].timestamp shouldBe 2000L
        points[1].value shouldBe 22.0
        points[2].timestamp shouldBe 3000L
        points[2].value shouldBe 23.0
    }

    @Test
    fun `buildTimeSeries handles empty history and missing metric keys gracefully`() {
        val emptyPoints = NumericPayloadExtractor.buildTimeSeries(emptyList(), "value")
        emptyPoints.shouldBeEmpty()

        val historyWithMissingKeys =
            listOf(
                MqttMessage(topic = "sensor/a", payload = """{"temp": 22.5}""".encodeToByteArray(), timestamp = 3000L),
                MqttMessage(topic = "sensor/a", payload = """{"humidity": 65}""".encodeToByteArray(), timestamp = 2000L),
                MqttMessage(topic = "sensor/a", payload = """{"temp": 20.0}""".encodeToByteArray(), timestamp = 1000L),
            )

        val tempPoints = NumericPayloadExtractor.buildTimeSeries(historyWithMissingKeys, "temp")
        tempPoints shouldHaveSize 2
        tempPoints[0].value shouldBe 20.0
        tempPoints[1].value shouldBe 22.5
    }

    @Test
    fun `extractMetrics ignores boolean and null JSON primitives`() {
        val json = """{"active": true, "error": null, "count": 10}"""
        val metrics = NumericPayloadExtractor.extractMetrics(json)
        metrics shouldHaveSize 1
        metrics["count"] shouldBe 10.0
    }
}
