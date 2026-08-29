package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.MqttMessage
import io.github.drlacheheb.mqtlin.domain.util.NumericPayloadExtractor
import io.github.drlacheheb.mqtlin.domain.util.TimeSeriesPoint
import io.github.drlacheheb.mqtlin.ui.theme.DarkBackground
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLow
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTertiary
import io.github.drlacheheb.mqtlin.ui.theme.MonoCode
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold

@Composable
fun TopicChartView(
    history: List<MqttMessage>,
    modifier: Modifier = Modifier
) {
    if (history.isEmpty()) {
        EmptyChartState("No message history available to chart.")
        return
    }

    val latestMessage = history.first()
    val availableMetrics = remember(latestMessage.payloadString) {
        NumericPayloadExtractor.extractMetrics(latestMessage.payloadString)
    }

    if (availableMetrics.isEmpty()) {
        NonNumericChartState()
        return
    }

    var selectedMetric by remember(availableMetrics.keys.toList()) {
        mutableStateOf(availableMetrics.keys.firstOrNull() ?: "value")
    }

    val timeSeries = remember(history, selectedMetric) {
        NumericPayloadExtractor.buildTimeSeries(history, selectedMetric)
    }

    if (timeSeries.isEmpty()) {
        EmptyChartState("No numeric values found for metric: $selectedMetric")
        return
    }

    val values = timeSeries.map { it.value }
    val currentValue = values.last()
    val minValue = values.minOrNull() ?: 0.0
    val maxValue = values.maxOrNull() ?: 0.0
    val avgValue = values.average()

    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        )
    )

    var hoveredPoint by remember { mutableStateOf<TimeSeriesPoint?>(null) }
    var hoverOffset by remember { mutableStateOf<Offset?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSurfaceContainerLow)
    ) {
        // 1. Top Controls Bar: Metric Selector Pills
        if (availableMetrics.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceContainer)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Metrics:",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = DarkOnSurfaceVariant
                )

                availableMetrics.keys.forEach { metricKey ->
                    val isSelected = metricKey == selectedMetric
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isSelected) MqtlinPrimary.copy(alpha = 0.20f) else DarkSurfaceDim,
                        border = BorderStroke(1.dp, if (isSelected) MqtlinPrimary else DarkOutlineVariant),
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable { selectedMetric = metricKey }
                    ) {
                        Text(
                            text = metricKey,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MqtlinPrimary else DarkOnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkOutlineVariant)
            )
        }

        // 2. Summary Statistics Ribbon
        Row(
            modifier = Modifier
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
            StatCard(label = "SAMPLES", value = "${timeSeries.size}", color = DarkOnSurfaceVariant)

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
                            text = formatTimestamp(hoveredPoint!!.timestamp),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = DarkOnSurfaceVariant
                        )
                        Text(
                            text = formatDouble(hoveredPoint!!.value),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MqtlinPrimary
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkOutlineVariant)
        )

        // 3. Canvas Time-Series Line Graph
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val paddingStart = 50f
            val paddingEnd = 20f
            val paddingTop = 25f
            val paddingBottom = 35f

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(timeSeries) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                when (event.type) {
                                    PointerEventType.Move -> {
                                        val pos = event.changes.first().position
                                        hoverOffset = pos
                                        val chartWidth = size.width - paddingStart - paddingEnd
                                        if (timeSeries.size > 1 && chartWidth > 0 && pos.x in paddingStart..(size.width - paddingEnd)) {
                                            val fraction = ((pos.x - paddingStart) / chartWidth).coerceIn(0f, 1f)
                                            val index = (fraction * (timeSeries.size - 1)).toInt().coerceIn(0, timeSeries.size - 1)
                                            hoveredPoint = timeSeries[index]
                                        } else {
                                            hoveredPoint = null
                                        }
                                    }
                                    PointerEventType.Exit -> {
                                        hoveredPoint = null
                                        hoverOffset = null
                                    }
                                }
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val plotWidth = canvasWidth - paddingStart - paddingEnd
                val plotHeight = canvasHeight - paddingTop - paddingBottom

                val range = if (maxValue == minValue) 1.0 else (maxValue - minValue)
                val effectiveMin = if (maxValue == minValue) minValue - 1.0 else minValue
                val effectiveMax = if (maxValue == minValue) maxValue + 1.0 else maxValue
                val effectiveRange = effectiveMax - effectiveMin

                // A. Draw Horizontal Gridlines (4 levels: 0%, 33%, 66%, 100%)
                val gridLinesCount = 4
                for (i in 0..gridLinesCount) {
                    val y = paddingTop + (plotHeight / gridLinesCount) * i
                    drawLine(
                        color = DarkOutlineVariant.copy(alpha = 0.35f),
                        start = Offset(paddingStart, y),
                        end = Offset(canvasWidth - paddingEnd, y),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                }

                // B. Map TimeSeries points to Coordinates
                val points = timeSeries.mapIndexed { index, point ->
                    val x = if (timeSeries.size <= 1) {
                        paddingStart + plotWidth / 2f
                    } else {
                        paddingStart + (index.toFloat() / (timeSeries.size - 1)) * plotWidth
                    }
                    val normalizedY = ((point.value - effectiveMin) / effectiveRange).toFloat().coerceIn(0f, 1f)
                    val y = paddingTop + plotHeight * (1f - normalizedY)
                    Offset(x, y)
                }

                if (points.isNotEmpty()) {
                    // C. Construct Gradient Fill Area Under Line
                    val fillPath = Path().apply {
                        moveTo(points.first().x, paddingTop + plotHeight)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, paddingTop + plotHeight)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MqtlinPrimary.copy(alpha = 0.28f),
                                MqtlinPrimary.copy(alpha = 0.02f)
                            ),
                            startY = paddingTop,
                            endY = paddingTop + plotHeight
                        )
                    )

                    // D. Draw Primary Smooth Line Stroke
                    val linePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            val p0 = points[i - 1]
                            val p1 = points[i]
                            val controlX = (p0.x + p1.x) / 2f
                            cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                        }
                    }

                    drawPath(
                        path = linePath,
                        color = MqtlinPrimary,
                        style = Stroke(
                            width = 2.5f.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // E. Draw Data Point Dots
                    points.forEachIndexed { idx, pt ->
                        val isLatest = idx == points.lastIndex
                        drawCircle(
                            color = if (isLatest) MqtlinSecondary else MqtlinPrimary,
                            radius = if (isLatest) 4.5f.dp.toPx() else 3f.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = DarkBackground,
                            radius = 1.5f.dp.toPx(),
                            center = pt
                        )
                    }

                    // F. Glowing pulse on latest point
                    val lastPt = points.last()
                    drawCircle(
                        color = MqtlinSecondary.copy(alpha = pulseAlpha),
                        radius = 8f.dp.toPx(),
                        center = lastPt,
                        style = Stroke(width = 2f)
                    )

                    // G. Hover Crosshair Guide
                    hoverOffset?.let { hover ->
                        if (hover.x in paddingStart..(canvasWidth - paddingEnd)) {
                            drawLine(
                                color = DarkOnSurface.copy(alpha = 0.5f),
                                start = Offset(hover.x, paddingTop),
                                end = Offset(hover.x, paddingTop + plotHeight),
                                strokeWidth = 1.5f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                            )
                        }
                    }
                }
            }

            // Y-Axis Labels overlay on the left
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(46.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatDouble(maxValue),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = DarkOutline
                )
                Text(
                    text = formatDouble((maxValue + minValue) / 2.0),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = DarkOutline
                )
                Text(
                    text = formatDouble(minValue),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = DarkOutline
                )
            }

            // X-Axis Timestamps overlay at the bottom
            if (timeSeries.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(start = 50.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTimestamp(timeSeries.first().timestamp),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = DarkOutline
                    )
                    if (timeSeries.size > 2) {
                        Text(
                            text = formatTimestamp(timeSeries[timeSeries.size / 2].timestamp),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = DarkOutline
                        )
                    }
                    Text(
                        text = formatTimestamp(timeSeries.last().timestamp),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = DarkOutline
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

@Composable
private fun EmptyChartState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ShowChart,
                contentDescription = null,
                tint = DarkOutlineVariant,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = message,
                fontSize = 13.sp,
                color = DarkOnSurfaceVariant.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun NonNumericChartState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MqtlinPrimary.copy(alpha = 0.7f),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Non-Numeric Payload",
                style = UiLabelBold.copy(fontSize = 14.sp, color = DarkOnSurface)
            )
            Text(
                text = "Real-time charts automatically visualize numeric messages (e.g. 21.5, 3.3V) or JSON objects containing numbers (e.g. {\"temperature\": 21.5}).",
                fontSize = 12.sp,
                color = DarkOnSurfaceVariant.copy(alpha = 0.8f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.width(360.dp)
            )
        }
    }
}

private fun formatDouble(value: Double): String {
    if (value == value.toLong().toDouble()) {
        return value.toLong().toString()
    }
    val rounded = (value * 100.0).toLong() / 100.0
    return rounded.toString()
}

private fun formatTimestamp(epochMs: Long): String {
    val seconds = (epochMs / 1000) % 86400
    val hours = (seconds / 3600) % 24
    val minutes = (seconds / 60) % 60
    val secs = seconds % 60
    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
}
