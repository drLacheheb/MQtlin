package io.github.drlacheheb.mqtlin.domain.util

enum class HexByteType {
    NULL,
    WHITESPACE,
    PRINTABLE_ASCII,
    CONTROL,
    NON_ASCII,
}

data class HexByte(
    val byte: Byte,
    val hexString: String,
    val displayChar: Char,
    val type: HexByteType,
)

data class HexRow(
    val offset: Int,
    val offsetHex: String,
    val bytes: List<HexByte>,
)

object HexUtils {
    fun classifyByte(b: Byte): HexByteType {
        val v = b.toInt() and 0xFF
        return when {
            v == 0 -> HexByteType.NULL
            v == 0x20 || v == 0x09 || v == 0x0A || v == 0x0D -> HexByteType.WHITESPACE
            v in 0x21..0x7E -> HexByteType.PRINTABLE_ASCII
            v < 0x20 || v == 0x7F -> HexByteType.CONTROL
            else -> HexByteType.NON_ASCII
        }
    }

    fun toDisplayChar(b: Byte): Char {
        val v = b.toInt() and 0xFF
        return if (v in 0x20..0x7E) v.toChar() else '·'
    }

    fun parseHexRows(
        bytes: ByteArray,
        chunkSize: Int = 16,
    ): List<HexRow> {
        if (bytes.isEmpty()) return emptyList()
        val rows = mutableListOf<HexRow>()
        for (i in bytes.indices step chunkSize) {
            val end = minOf(i + chunkSize, bytes.size)
            val chunk = bytes.sliceArray(i until end)
            val hexBytes =
                chunk.map { b ->
                    val hex = (b.toInt() and 0xFF).toString(16).padStart(2, '0').uppercase()
                    HexByte(
                        byte = b,
                        hexString = hex,
                        displayChar = toDisplayChar(b),
                        type = classifyByte(b),
                    )
                }
            rows.add(
                HexRow(
                    offset = i,
                    offsetHex = i.toString(16).padStart(8, '0').uppercase(),
                    bytes = hexBytes,
                ),
            )
        }
        return rows
    }

    fun formatHexDump(bytes: ByteArray): String {
        if (bytes.isEmpty()) return "00000000:  (empty payload)"
        val rows = parseHexRows(bytes)
        val sb = StringBuilder()
        for (row in rows) {
            sb.append("${row.offsetHex}:  ")
            val hexPart1 = row.bytes.take(8).joinToString(" ") { it.hexString }
            val hexPart2 = row.bytes.drop(8).joinToString(" ") { it.hexString }
            val hexCombined = if (hexPart2.isNotEmpty()) "$hexPart1  $hexPart2" else hexPart1
            val paddedHex = hexCombined.padEnd(49, ' ')
            val ascii =
                row.bytes
                    .map {
                        val v = it.byte.toInt() and 0xFF
                        if (v in 32..126) v.toChar() else '.'
                    }.joinToString("")
            sb
                .append(paddedHex)
                .append("  |")
                .append(ascii)
                .append("|\n")
        }
        return sb.toString().trimEnd()
    }
}
