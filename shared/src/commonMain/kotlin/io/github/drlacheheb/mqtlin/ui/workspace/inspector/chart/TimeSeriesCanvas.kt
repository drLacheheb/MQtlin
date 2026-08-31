package io.github.drlacheheb.mqtlin.ui.workspace.inspector.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.util.TimeSeriesPoint
import io.github.drlacheheb.mqtlin.ui.theme.DarkBackground
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary

/**
 * Canvas rendering the smooth time-series cubic bezier line, gradient underfill, gridlines, axis labels, and hover cursor.
 */
@Composable
fun TimeSeriesCanvas(
    timeSeries: List<TimeSeriesPoint>,
    minValue: Double,
    maxValue: Double,
    pulseAlpha: Float,
    hoverOffset: Offset?,
    onHoverChange: (Offset?, TimeSeriesPoint?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        val paddingStart = 50f
        val paddingEnd = 20f
        val paddingTop = 25f
        val paddingBottom = 35f

        Canvas(
            modifier =
                Modifier
                    .fillMaxSize()
                    .pointerInput(timeSeries) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                when (event.type) {
                                    PointerEventType.Move -> {
                                        val pos = event.changes.first().position
                                        val chartWidth = size.width - paddingStart - paddingEnd
                                        if (timeSeries.size > 1 && chartWidth > 0 && pos.x in paddingStart..(size.width - paddingEnd)) {
                                            val fraction = ((pos.x - paddingStart) / chartWidth).coerceIn(0f, 1f)
                                            val index = (fraction * (timeSeries.size - 1)).toInt().coerceIn(0, timeSeries.size - 1)
                                            onHoverChange(pos, timeSeries[index])
                                        } else {
                                            onHoverChange(pos, null)
                                        }
                                    }
                                    PointerEventType.Exit -> {
                                        onHoverChange(null, null)
                                    }
                                }
                            }
                        }
                    },
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val plotWidth = canvasWidth - paddingStart - paddingEnd
            val plotHeight = canvasHeight - paddingTop - paddingBottom

            val effectiveMin = if (maxValue == minValue) minValue - 1.0 else minValue
            val effectiveMax = if (maxValue == minValue) maxValue + 1.0 else maxValue
            val effectiveRange = effectiveMax - effectiveMin

            // 1. Draw Horizontal Gridlines
            val gridLinesCount = 4
            for (i in 0..gridLinesCount) {
                val y = paddingTop + (plotHeight / gridLinesCount) * i
                drawLine(
                    color = DarkOutlineVariant.copy(alpha = 0.35f),
                    start = Offset(paddingStart, y),
                    end = Offset(canvasWidth - paddingEnd, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
                )
            }

            // 2. Map TimeSeries points to Coordinates
            val points =
                timeSeries.mapIndexed { index, point ->
                    val x =
                        if (timeSeries.size <= 1) {
                            paddingStart + plotWidth / 2f
                        } else {
                            paddingStart + (index.toFloat() / (timeSeries.size - 1)) * plotWidth
                        }
                    val normalizedY = ((point.value - effectiveMin) / effectiveRange).toFloat().coerceIn(0f, 1f)
                    val y = paddingTop + plotHeight * (1f - normalizedY)
                    Offset(x, y)
                }

            if (points.isNotEmpty()) {
                // 3. Gradient Fill Under Line
                val fillPath =
                    Path().apply {
                        moveTo(points.first().x, paddingTop + plotHeight)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, paddingTop + plotHeight)
                        close()
                    }

                drawPath(
                    path = fillPath,
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    MqtlinPrimary.copy(alpha = 0.28f),
                                    MqtlinPrimary.copy(alpha = 0.02f),
                                ),
                            startY = paddingTop,
                            endY = paddingTop + plotHeight,
                        ),
                )

                // 4. Smooth Cubic Bezier Stroke
                val linePath =
                    Path().apply {
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
                    style =
                        Stroke(
                            width = 2.5f.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                )

                // 5. Data Points
                points.forEachIndexed { idx, pt ->
                    val isLatest = idx == points.lastIndex
                    drawCircle(
                        color = if (isLatest) MqtlinSecondary else MqtlinPrimary,
                        radius = if (isLatest) 4.5f.dp.toPx() else 3f.dp.toPx(),
                        center = pt,
                    )
                    drawCircle(
                        color = DarkBackground,
                        radius = 1.5f.dp.toPx(),
                        center = pt,
                    )
                }

                // 6. Glowing Pulse on Latest Point
                val lastPt = points.last()
                drawCircle(
                    color = MqtlinSecondary.copy(alpha = pulseAlpha),
                    radius = 8f.dp.toPx(),
                    center = lastPt,
                    style = Stroke(width = 2f),
                )

                // 7. Hover Crosshair Guide
                hoverOffset?.let { hover ->
                    if (hover.x in paddingStart..(canvasWidth - paddingEnd)) {
                        drawLine(
                            color = DarkOnSurface.copy(alpha = 0.5f),
                            start = Offset(hover.x, paddingTop),
                            end = Offset(hover.x, paddingTop + plotHeight),
                            strokeWidth = 1.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f),
                        )
                    }
                }
            }
        }

        // Y-Axis Labels overlay on the left
        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .width(46.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = formatDouble(maxValue),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = DarkOutline,
            )
            Text(
                text = formatDouble((maxValue + minValue) / 2.0),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = DarkOutline,
            )
            Text(
                text = formatDouble(minValue),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = DarkOutline,
            )
        }

        // X-Axis Timestamps overlay at the bottom
        if (timeSeries.isNotEmpty()) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(start = 50.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = formatChartTimestamp(timeSeries.first().timestamp),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = DarkOutline,
                )
                if (timeSeries.size > 2) {
                    Text(
                        text = formatChartTimestamp(timeSeries[timeSeries.size / 2].timestamp),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = DarkOutline,
                    )
                }
                Text(
                    text = formatChartTimestamp(timeSeries.last().timestamp),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = DarkOutline,
                )
            }
        }
    }
}
