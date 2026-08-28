package io.github.drlacheheb.mqtlin.domain.util

object HexUtils {

    fun formatHexDump(bytes: ByteArray): String {
        if (bytes.isEmpty()) return "00000000:  (empty payload)"

        val sb = StringBuilder()
        val chunkSize = 16

        for (i in bytes.indices step chunkSize) {
            val offsetHex = i.toString(16).padStart(8, '0').uppercase()
            sb.append("$offsetHex:  ")

            val end = minOf(i + chunkSize, bytes.size)
            val chunk = bytes.sliceArray(i until end)

            val hexBytes = chunk.joinToString(" ") { b ->
                (b.toInt() and 0xFF).toString(16).padStart(2, '0').uppercase()
            }
            val paddedHex = hexBytes.padEnd(chunkSize * 3, ' ')

            val ascii = chunk.map { b ->
                val byteVal = b.toInt() and 0xFF
                if (byteVal in 32..126) byteVal.toChar() else '.'
            }.joinToString("")

            sb.append(paddedHex).append("  |").append(ascii).append("|\n")
        }

        return sb.toString().trimEnd()
    }
}

