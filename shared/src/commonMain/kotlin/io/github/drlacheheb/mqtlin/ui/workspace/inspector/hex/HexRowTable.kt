package io.github.drlacheheb.mqtlin.ui.workspace.inspector.hex

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import io.github.drlacheheb.mqtlin.domain.util.HexRow
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLow

// Zebra Striping Row Background
private val HexRowStripeBg = Color(0xFF18181D)

// Fixed Table Geometry Dimensions
private val HexColumnWidth = 475.dp

val HexMonoStyle =
    TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        lineHeight = 20.sp,
    )

/**
 * Scrollable table displaying offset addresses, 16-byte hex dump columns, and ASCII text representation.
 */
@Composable
fun HexRowTable(
    hexRows: List<HexRow>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .fillMaxHeight(),
    ) {
        // ==================== TABLE THEAD (HEADER ROW) ====================
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceContainerLow)
                    .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // TH 1: Offset & Hex Ruler (Fixed 475dp)
            Box(
                modifier =
                    Modifier
                        .width(HexColumnWidth)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "OFFSET    00 01 02 03 04 05 06 07   08 09 0A 0B 0C 0D 0E 0F",
                    style = HexMonoStyle.copy(fontWeight = FontWeight.Bold, color = DarkOutline),
                )
            }

            // Center 1dp Vertical Table Border
            Box(
                modifier =
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(DarkOutlineVariant),
            )

            // TH 2: Decoded Text Title
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "DECODED TEXT",
                    style = HexMonoStyle.copy(fontWeight = FontWeight.Bold, color = DarkOutline),
                )
            }
        }

        // Table Header Bottom 1dp Border
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkOutlineVariant),
        )

        // ==================== TABLE TBODY (DATA ROWS) ====================
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
        ) {
            hexRows.forEachIndexed { index, row ->
                val isStripe = index % 2 == 1
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(if (isStripe) HexRowStripeBg else Color.Transparent)
                            .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // TD 1: Hex Bytes (Fixed 475dp)
                    Box(
                        modifier =
                            Modifier
                                .width(HexColumnWidth)
                                .padding(horizontal = 16.dp, vertical = 3.dp),
                    ) {
                        HexBytesText(row = row)
                    }

                    // Center 1dp Vertical Table Border
                    Box(
                        modifier =
                            Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(DarkOutlineVariant),
                    )

                    // TD 2: Decoded Text
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 3.dp),
                    ) {
                        DecodedText(row = row)
                    }
                }
            }
        }

        // Table Full-Height Continuous Grid Extension
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(IntrinsicSize.Min),
        ) {
            Box(modifier = Modifier.width(HexColumnWidth))
            Box(
                modifier =
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(DarkOutlineVariant),
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun HexBytesText(row: HexRow) {
    val annotated =
        buildAnnotatedString {
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

            // Second 8 Bytes
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
        style = HexMonoStyle,
    )
}

@Composable
private fun DecodedText(row: HexRow) {
    val annotated =
        buildAnnotatedString {
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
        style = HexMonoStyle,
    )
}
