package io.github.drlacheheb.mqtlin.ui.workspace.inspector.chart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import io.github.drlacheheb.mqtlin.domain.util.TimeSeriesPoint
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiary

/**
 * Top summary statistics ribbon for time-series charts (CURRENT, MIN, MAX, AVG, SAMPLES, hovered point tooltip).
 */
@Composable
fun TimeSeriesStatsRibbon(
    currentValue: Double,
    minValue: Double,
    maxValue: Double,
    avgValue: Double,
    sampleCount: Int,
    hoveredPoint: TimeSeriesPoint?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurfaceDim)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatCard(label = "CURRENT", value = formatDouble(currentValue), color = MqtlinPrimary)
        StatCard(label = "MIN", value = formatDouble(minValue), color = MqtlinSecondary)
        StatCard(label = "MAX", value = formatDouble(maxValue), color = MqtlinTertiary)
        StatCard(label = "AVG", value = formatDouble(avgValue), color = DarkOnSurface)
        StatCard(label = "SAMPLES", value = "$sampleCount", color = DarkOnSurfaceVariant)

        Spacer(modifier = Modifier.weight(1f))

        if (hoveredPoint != null) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = DarkSurfaceContainer,
                border = BorderStroke(1.dp, MqtlinPrimary.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = formatChartTimestamp(hoveredPoint.timestamp),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = DarkOnSurfaceVariant
                    )
                    Text(
                        text = formatDouble(hoveredPoint.value),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MqtlinPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = DarkOnSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

fun formatDouble(value: Double): String {
    if (value == value.toLong().toDouble()) {
        return value.toLong().toString()
    }
    val rounded = (value * 100.0).toLong() / 100.0
    return rounded.toString()
}

fun formatChartTimestamp(epochMs: Long): String {
    val seconds = (epochMs / 1000) % 86400
    val hours = (seconds / 3600) % 24
    val minutes = (seconds / 60) % 60
    val secs = seconds % 60
    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
}
