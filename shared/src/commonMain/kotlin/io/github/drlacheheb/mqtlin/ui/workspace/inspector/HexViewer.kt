package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.util.HexByteType
import io.github.drlacheheb.mqtlin.domain.util.HexRow
import io.github.drlacheheb.mqtlin.domain.util.HexUtils
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLow
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLowest
import io.github.drlacheheb.mqtlin.ui.theme.MonoCode

// Semantic Hex Color Palette (Standard hexyl / ImHex palette)
private val HexColorNull = Color(0xFF52525B)          // Muted Zinc Gray
private val HexColorPrintable = Color(0xFF38BDF8)     // Sky Blue
private val HexColorWhitespace = Color(0xFF4ADE80)    // Emerald Green
private val HexColorControl = Color(0xFFC084FC)       // Lavender Purple
private val HexColorNonAscii = Color(0xFFFB923C)      // Amber Orange

// Zebra Striping Row Background
private val HexRowStripeBg = Color(0xFF18181D)

// Fixed Table Geometry Dimensions
private val HexColumnWidth = 475.dp

private val HexMonoStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 12.sp,
    lineHeight = 20.sp
)

private fun getByteColor(type: HexByteType): Color {
    return when (type) {
        HexByteType.NULL -> HexColorNull
        HexByteType.PRINTABLE_ASCII -> HexColorPrintable
        HexByteType.WHITESPACE -> HexColorWhitespace
        HexByteType.CONTROL -> HexColorControl
        HexByteType.NON_ASCII -> HexColorNonAscii
    }
}

@Composable
fun HexViewer(
    payload: ByteArray,
    modifier: Modifier = Modifier
) {
    val hexRows = remember(payload) { HexUtils.parseHexRows(payload) }
    val verticalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSurfaceContainerLowest)
    ) {
        if (payload.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Payload is empty (0 Bytes)",
                    style = HexMonoStyle.copy(fontSize = 13.sp, color = DarkOutline)
                )
            }
        } else {
            // Full-Width Table Viewport
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(verticalScrollState)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    // ==================== TABLE THEAD (HEADER ROW) ====================
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceContainerLow)
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // TH 1: Offset & Hex Ruler (Fixed 475dp)
                        Box(
                            modifier = Modifier
                                .width(HexColumnWidth)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "OFFSET    00 01 02 03 04 05 06 07   08 09 0A 0B 0C 0D 0E 0F",
                                style = HexMonoStyle.copy(fontWeight = FontWeight.Bold, color = DarkOutline)
                            )
                        }

                        // Center 1dp Vertical Table Border
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(DarkOutlineVariant)
                        )

                        // TH 2: Decoded Text Title (Takes all remaining width to right edge)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "DECODED TEXT",
                                style = HexMonoStyle.copy(fontWeight = FontWeight.Bold, color = DarkOutline)
                            )
                        }
                    }

                    // Table Header Bottom 1dp Border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(DarkOutlineVariant)
                    )

                    // ==================== TABLE TBODY (DATA ROWS) ====================
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        hexRows.forEachIndexed { index, row ->
                            val isStripe = index % 2 == 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isStripe) HexRowStripeBg else Color.Transparent)
                                    .height(IntrinsicSize.Min),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // TD 1: Hex Bytes (Fixed 475dp)
                                Box(
                                    modifier = Modifier
                                        .width(HexColumnWidth)
                                        .padding(horizontal = 16.dp, vertical = 3.dp)
                                ) {
                                    HexBytesText(row = row)
                                }

                                // Center 1dp Vertical Table Border
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .fillMaxHeight()
                                        .background(DarkOutlineVariant)
                                )

                                // TD 2: Decoded Text (Takes all remaining width to right edge)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 3.dp)
                                ) {
                                    DecodedText(row = row)
                                }
                            }
                        }
                    }

                    // Table Full-Height Continuous Grid Extension (Full width & height)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(IntrinsicSize.Min)
                    ) {
                        Box(modifier = Modifier.width(HexColumnWidth))
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(DarkOutlineVariant)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Bottom Legend & Metadata Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkOutlineVariant)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkSurfaceContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Color Legend
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HexLegendChip(color = HexColorNull, label = "Null (00)")
                        HexLegendChip(color = HexColorPrintable, label = "ASCII")
                        HexLegendChip(color = HexColorWhitespace, label = "Whitespace")
                        HexLegendChip(color = HexColorControl, label = "Control")
                        HexLegendChip(color = HexColorNonAscii, label = "Non-ASCII")
                    }

                    // Total Bytes Badge
                    Text(
                        text = "${payload.size} Bytes",
                        style = MonoCode.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkOutline)
                    )
                }
            }
        }
    }
}

@Composable
private fun HexBytesText(row: HexRow) {
    val annotated = buildAnnotatedString {
        // Offset Column (10 chars: "00000000: ")
        withStyle(SpanStyle(color = DarkOutline, fontWeight = FontWeight.Medium)) {
            append("${row.offsetHex}: ")
        }

        // First 8 Bytes (24 chars: 8 * 3)
        val firstHalf = row.bytes.take(8)
        for (hb in firstHalf) {
            withStyle(SpanStyle(color = getByteColor(hb.type), fontWeight = FontWeight.SemiBold)) {
                append("${hb.hexString} ")
            }
        }
        if (firstHalf.size < 8) {
            append("   ".repeat(8 - firstHalf.size))
        }

        // Mid Gap (2 chars: "  ")
        append("  ")

        // Second 8 Bytes (23 chars: 7 * 3 + 2 = 23)
        val secondHalf = row.bytes.drop(8)
        for (i in 0 until 8) {
            val hb = secondHalf.getOrNull(i)
            if (hb != null) {
                withStyle(SpanStyle(color = getByteColor(hb.type), fontWeight = FontWeight.SemiBold)) {
                    append(hb.hexString)
                }
                if (i < 7) append(" ")
            } else {
                append("  ")
                if (i < 7) append(" ")
            }
        }
    }

    Text(
        text = annotated,
        style = HexMonoStyle
    )
}

@Composable
private fun DecodedText(row: HexRow) {
    val annotated = buildAnnotatedString {
        for (hb in row.bytes) {
            withStyle(SpanStyle(color = getByteColor(hb.type), fontWeight = FontWeight.Medium)) {
                append(hb.displayChar)
            }
        }
        if (row.bytes.size < 16) {
            append(" ".repeat(16 - row.bytes.size))
        }
    }

    Text(
        text = annotated,
        style = HexMonoStyle
    )
}

@Composable
private fun HexLegendChip(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, shape = CircleShape)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = DarkOutline
        )
    }
}
