package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import io.github.drlacheheb.mqtlin.ui.workspace.inspector.chart.formatChartTimestamp
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.chart.formatDouble
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.header.formatInspectorTimestamp
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class InspectorFormattingTest {
    @Test
    fun `formatInspectorTimestamp formats milliseconds to HH-mm-ss-SSS`() {
        // 00:00:00.000 epoch
        formatInspectorTimestamp(0L) shouldBe "00:00:00.000"

        // 3661500 ms -> 01:01:01.500
        val ms = (1 * 3600 + 1 * 60 + 1) * 1000L + 500L
        formatInspectorTimestamp(ms) shouldBe "01:01:01.500"
    }

    @Test
    fun `formatChartTimestamp formats milliseconds to HH-mm-ss`() {
        formatChartTimestamp(0L) shouldBe "00:00:00"

        val ms = (14 * 3600 + 30 * 60 + 45) * 1000L
        formatChartTimestamp(ms) shouldBe "14:30:45"
    }

    @Test
    fun `formatDouble formats whole numbers without decimals and fractional numbers with up to two decimals`() {
        formatDouble(25.0) shouldBe "25"
        formatDouble(0.0) shouldBe "0"
        formatDouble(-10.0) shouldBe "-10"
        formatDouble(25.5) shouldBe "25.5"
        formatDouble(25.123) shouldBe "25.12"
        formatDouble(25.99) shouldBe "25.99"
    }
}
