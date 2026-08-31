package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.util.DiffLine
import io.github.drlacheheb.mqtlin.domain.util.DiffResult
import io.github.drlacheheb.mqtlin.domain.util.DiffType
import io.github.drlacheheb.mqtlin.domain.util.DiffUtils
import io.github.drlacheheb.mqtlin.ui.theme.DarkBackground
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary

private val DiffAddedBg = Color(0xFF142B1F)
private val DiffAddedText = Color(0xFF4EDEA3)
private val DiffDeletedBg = Color(0xFF331417)
private val DiffDeletedText = Color(0xFFFF7A7A)

@Composable
fun DiffView(
    oldText: String,
    newText: String,
    oldLabel: String = "Previous Payload",
    newLabel: String = "Current Payload",
    modifier: Modifier = Modifier
) {
    val diffResult: DiffResult = DiffUtils.computeDiff(oldText = oldText, newText = newText)
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSurfaceDim)
    ) {
        // Summary Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceContainer)
                .border(1.dp, DarkOutlineVariant)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = null,
                    tint = MqtlinPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$oldLabel vs $newLabel",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    color = DarkOnSurface
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "+${diffResult.additions}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (diffResult.additions > 0) DiffAddedText else DarkOutlineVariant
                )
                Text(
                    text = "-${diffResult.deletions}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (diffResult.deletions > 0) DiffDeletedText else DarkOutlineVariant
                )
            }
        }

        // Diff Lines Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                diffResult.lines.forEach { line ->
                    DiffLineRow(line = line)
                }
            }
        }
    }
}

@Composable
private fun DiffLineRow(line: DiffLine) {
    val (bg, textColor, symbol) = when (line.type) {
        DiffType.ADDED -> Triple(DiffAddedBg, DiffAddedText, "+")
        DiffType.DELETED -> Triple(DiffDeletedBg, DiffDeletedText, "-")
        DiffType.UNCHANGED -> Triple(Color.Transparent, DarkOnSurface, " ")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gutter: Line Numbers
        Row(
            modifier = Modifier
                .width(72.dp)
                .background(DarkBackground.copy(alpha = 0.5f))
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = line.oldLineNumber?.toString() ?: "",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = DarkOutline,
                textAlign = TextAlign.End,
                modifier = Modifier.width(28.dp)
            )
            Text(
                text = line.newLineNumber?.toString() ?: "",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = DarkOutline,
                textAlign = TextAlign.End,
                modifier = Modifier.width(28.dp)
            )
        }

        // Sign Symbol (+ / - / space)
        Text(
            text = symbol,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(20.dp)
        )

        // Line Content
        Text(
            text = line.text,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = textColor,
            lineHeight = 18.sp,
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}
