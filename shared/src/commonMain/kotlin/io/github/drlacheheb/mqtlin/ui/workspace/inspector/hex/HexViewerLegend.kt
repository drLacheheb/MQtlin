package io.github.drlacheheb.mqtlin.ui.workspace.inspector.hex

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.util.HexByteType
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.MonoCode

// Semantic Hex Color Palette (Standard hexyl / ImHex palette)
val HexColorNull = Color(0xFF52525B)          // Muted Zinc Gray
val HexColorPrintable = Color(0xFF38BDF8)     // Sky Blue
val HexColorWhitespace = Color(0xFF4ADE80)    // Emerald Green
val HexColorControl = Color(0xFFC084FC)       // Lavender Purple
val HexColorNonAscii = Color(0xFFFB923C)      // Amber Orange

fun getByteColor(type: HexByteType): Color {
    return when (type) {
        HexByteType.NULL -> HexColorNull
        HexByteType.PRINTABLE_ASCII -> HexColorPrintable
        HexByteType.WHITESPACE -> HexColorWhitespace
        HexByteType.CONTROL -> HexColorControl
        HexByteType.NON_ASCII -> HexColorNonAscii
    }
}

/**
 * Bottom legend explaining byte classification colors and total byte count.
 */
@Composable
fun HexViewerLegend(
    totalBytes: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
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
                    text = "$totalBytes Bytes",
                    style = MonoCode.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkOutline)
                )
            }
        }
    }
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
