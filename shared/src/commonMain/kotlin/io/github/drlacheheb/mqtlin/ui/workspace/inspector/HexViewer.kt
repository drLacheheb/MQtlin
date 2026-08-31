package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.util.HexUtils
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLowest
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.hex.HexMonoStyle
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.hex.HexRowTable
import io.github.drlacheheb.mqtlin.ui.workspace.inspector.hex.HexViewerLegend

/**
 * Main Hexadecimal Byte Inspector View with structured data columns and classification legend.
 */
@Composable
fun HexViewer(
    payload: ByteArray,
    modifier: Modifier = Modifier,
) {
    val hexRows = remember(payload) { HexUtils.parseHexRows(payload) }
    val verticalScrollState = rememberScrollState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(DarkSurfaceContainerLowest),
    ) {
        if (payload.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Payload is empty (0 Bytes)",
                    style = HexMonoStyle.copy(fontSize = 13.sp, color = DarkOutline),
                )
            }
        } else {
            // Full-Width Scrollable Table Viewport
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(verticalScrollState),
            ) {
                HexRowTable(hexRows = hexRows)
            }

            // Bottom Category Color Legend & Byte Counter
            HexViewerLegend(totalBytes = payload.size)
        }
    }
}
