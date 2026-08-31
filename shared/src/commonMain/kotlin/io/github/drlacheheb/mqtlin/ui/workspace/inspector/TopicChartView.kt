package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.util.NumericPayloadExtractor
import io.github.drlacheheb.mqtlin.domain.util.TimeSeriesPoint
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLowest
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.chart.TimeSeriesCanvas
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.chart.TimeSeriesStatsRibbon

/**
 * Time-Series charting view for topics with numeric payloads.
 */
@Composable
fun TopicChartView(
    history: List<MqttMessage>,
    modifier: Modifier = Modifier,
) {
    val availableMetrics =
        remember(history) {
            val latestPayload = history.firstOrNull()?.payloadString ?: ""
            NumericPayloadExtractor.extractMetrics(latestPayload).keys.toList()
        }

    var selectedMetric by remember(availableMetrics) {
        mutableStateOf(availableMetrics.firstOrNull() ?: "value")
    }

    val timeSeries =
        remember(history, selectedMetric) {
            NumericPayloadExtractor.buildTimeSeries(history, selectedMetric)
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(DarkSurfaceContainerLowest),
    ) {
        if (availableMetrics.isEmpty() && timeSeries.isEmpty()) {
            EmptyChartState(
                reason = "The messages received for this topic do not contain parsable numeric scalar or JSON metric values.",
                modifier = Modifier.weight(1f),
            )
        } else if (timeSeries.size < 2) {
            EmptyChartState(
                reason = "Only 1 numeric sample has been recorded so far. More messages are needed to render a trend graph.",
                modifier = Modifier.weight(1f),
            )
        } else {
            var hoveredPoint by remember { mutableStateOf<TimeSeriesPoint?>(null) }
            var hoverOffset by remember { mutableStateOf<Offset?>(null) }

            val values = timeSeries.map { it.value }
            val minValue = values.minOrNull() ?: 0.0
            val maxValue = values.maxOrNull() ?: 0.0
            val currentValue = timeSeries.lastOrNull()?.value ?: 0.0
            val avgValue = if (values.isNotEmpty()) values.average() else 0.0

            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 0.8f,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse,
                    ),
                label = "pulseAlpha",
            )

            // 1. Chart Header & Legend
            ChartHeaderBar(metricKey = if (selectedMetric != "value") selectedMetric else null)

            // 2. Summary Statistics Ribbon
            TimeSeriesStatsRibbon(
                currentValue = currentValue,
                minValue = minValue,
                maxValue = maxValue,
                avgValue = avgValue,
                sampleCount = timeSeries.size,
                hoveredPoint = hoveredPoint,
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DarkOutlineVariant),
            )

            // 3. Canvas Time-Series Line Graph
            TimeSeriesCanvas(
                timeSeries = timeSeries,
                minValue = minValue,
                maxValue = maxValue,
                pulseAlpha = pulseAlpha,
                hoverOffset = hoverOffset,
                onHoverChange = { offset, pt ->
                    hoverOffset = offset
                    hoveredPoint = pt
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ChartHeaderBar(
    metricKey: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(DarkSurfaceContainer)
                    .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                    contentDescription = null,
                    tint = MqtlinPrimary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = if (metricKey != null) "Time-Series Plot ($metricKey)" else "Time-Series Plot",
                    style = UiLabelBold.copy(fontSize = 13.sp, color = DarkOnSurface),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LegendItem(color = MqtlinPrimary, label = "Value Trend")
                LegendItem(color = MqtlinSecondary, label = "Latest Sample")
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkOutlineVariant),
        )
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(10.dp)
                    .background(color, RoundedCornerShape(2.dp)),
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = DarkOnSurfaceVariant,
        )
    }
}

@Composable
private fun EmptyChartState(
    reason: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ShowChart,
                contentDescription = null,
                tint = DarkOutlineVariant,
                modifier = Modifier.size(36.dp),
            )
            Text(
                text = "No Chart Data Available",
                style = UiLabelBold.copy(fontSize = 14.sp, color = DarkOnSurface),
            )
            Text(
                text = reason,
                fontSize = 12.sp,
                color = DarkOnSurfaceVariant.copy(alpha = 0.8f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.width(360.dp),
            )
        }
    }
}
