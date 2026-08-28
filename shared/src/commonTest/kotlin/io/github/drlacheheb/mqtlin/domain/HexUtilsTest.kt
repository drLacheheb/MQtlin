package io.github.drlacheheb.mqtlin.domain

import io.github.drlacheheb.mqtlin.domain.util.HexUtils
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class HexUtilsTest {

    @Test
    fun `empty byte array returns empty payload placeholder`() {
        val result = HexUtils.formatHexDump(ByteArray(0))
        result shouldBe "00000000:  (empty payload)"
    }

    @Test
    fun `single line ASCII payload formats offset, hex bytes and printable characters`() {
        val bytes = "Hello MQTT".encodeToByteArray()
        val result = HexUtils.formatHexDump(bytes)

        result shouldContain "00000000:  "
        result shouldContain "48 65 6C 6C 6F 20 4D 51 54 54"
        result shouldContain "|Hello MQTT|"
    }

    @Test
    fun `multi-line payload exceeding 16 bytes wraps with incremented hex offsets`() {
        val text = "This is a longer payload that spans multiple lines of hex dump output"
        val bytes = text.encodeToByteArray()
        val result = HexUtils.formatHexDump(bytes)

        val lines = result.lines()
        lines.size shouldBe 5
        lines[0] shouldContain "00000000:  "
        lines[1] shouldContain "00000010:  "
        lines[2] shouldContain "00000020:  "
        lines[3] shouldContain "00000030:  "
        lines[4] shouldContain "00000040:  "
    }

    @Test
    fun `binary non-printable control bytes are replaced with dots in ASCII column`() {
        val binaryData = byteArrayOf(0x00, 0x01, 0x02, 0x41, 0x42, 0x7F, 0xFF.toByte())
        val result = HexUtils.formatHexDump(binaryData)

        result shouldContain "00 01 02 41 42 7F FF"
        result shouldContain "|...AB..|"
    }
}

