package io.github.drlacheheb.mqtlin.domain

import io.github.drlacheheb.mqtlin.domain.util.HexByteType
import io.github.drlacheheb.mqtlin.domain.util.HexUtils
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class HexUtilsTest {

    @Test
    fun `classifyByte correctly categorizes byte values`() {
        // Null
        HexUtils.classifyByte(0x00.toByte()) shouldBe HexByteType.NULL

        // Whitespace
        HexUtils.classifyByte(' '.code.toByte()) shouldBe HexByteType.WHITESPACE
        HexUtils.classifyByte('\n'.code.toByte()) shouldBe HexByteType.WHITESPACE
        HexUtils.classifyByte('\t'.code.toByte()) shouldBe HexByteType.WHITESPACE
        HexUtils.classifyByte('\r'.code.toByte()) shouldBe HexByteType.WHITESPACE

        // Printable ASCII
        HexUtils.classifyByte('A'.code.toByte()) shouldBe HexByteType.PRINTABLE_ASCII
        HexUtils.classifyByte('0'.code.toByte()) shouldBe HexByteType.PRINTABLE_ASCII
        HexUtils.classifyByte('{'.code.toByte()) shouldBe HexByteType.PRINTABLE_ASCII

        // Control
        HexUtils.classifyByte(0x01.toByte()) shouldBe HexByteType.CONTROL
        HexUtils.classifyByte(0x1B.toByte()) shouldBe HexByteType.CONTROL
        HexUtils.classifyByte(0x7F.toByte()) shouldBe HexByteType.CONTROL

        // Non-ASCII
        HexUtils.classifyByte(0x80.toByte()) shouldBe HexByteType.NON_ASCII
        HexUtils.classifyByte(0xFF.toByte()) shouldBe HexByteType.NON_ASCII
    }

    @Test
    fun `parseHexRows slices payload into 16-byte chunks with correct offsets`() {
        val payload = ByteArray(35) { it.toByte() }
        val rows = HexUtils.parseHexRows(payload)

        rows shouldHaveSize 3
        rows[0].offset shouldBe 0
        rows[0].offsetHex shouldBe "00000000"
        rows[0].bytes shouldHaveSize 16

        rows[1].offset shouldBe 16
        rows[1].offsetHex shouldBe "00000010"
        rows[1].bytes shouldHaveSize 16

        rows[2].offset shouldBe 32
        rows[2].offsetHex shouldBe "00000020"
        rows[2].bytes shouldHaveSize 3
    }

    @Test
    fun `formatHexDump formats multi-line hex dump with ASCII sidebar`() {
        val text = "Hello World!"
        val dump = HexUtils.formatHexDump(text.encodeToByteArray())

        dump shouldContain "00000000:"
        dump shouldContain "48 65 6C 6C 6F 20 57 6F  72 6C 64 21"
        dump shouldContain "|Hello World!|"
    }

    @Test
    fun `empty payload returns empty marker`() {
        val dump = HexUtils.formatHexDump(ByteArray(0))
        dump shouldContain "empty payload"
    }
}
