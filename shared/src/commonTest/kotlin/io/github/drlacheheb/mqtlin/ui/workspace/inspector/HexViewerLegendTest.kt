package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import io.github.drlacheheb.mqtlin.domain.util.HexByteType
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.hex.HexColorControl
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.hex.HexColorNonAscii
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.hex.HexColorNull
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.hex.HexColorPrintable
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.hex.HexColorWhitespace
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.hex.getByteColor
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class HexViewerLegendTest {

    @Test
    fun `getByteColor maps each HexByteType to its semantic palette color`() {
        getByteColor(HexByteType.NULL) shouldBe HexColorNull
        getByteColor(HexByteType.PRINTABLE_ASCII) shouldBe HexColorPrintable
        getByteColor(HexByteType.WHITESPACE) shouldBe HexColorWhitespace
        getByteColor(HexByteType.CONTROL) shouldBe HexColorControl
        getByteColor(HexByteType.NON_ASCII) shouldBe HexColorNonAscii
    }
}
