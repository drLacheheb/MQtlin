package io.github.drlacheheb.mqtlin.ui.workspace.inspector

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLowest
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MonoCode
import io.github.drlacheheb.mqtlin.ui.util.highlightJson

/**
 * Code viewer with line numbers gutter and syntax highlighting for JSON and formatted text.
 */
@Composable
fun PayloadCodeViewer(
    text: String,
    isJson: Boolean,
    modifier: Modifier = Modifier,
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val lines = text.lines()
    val lineCount = if (lines.isEmpty()) 1 else lines.size

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(DarkSurfaceContainerLowest),
    ) {
        // Line Numbers Gutter
        Column(
            modifier =
                Modifier
                    .width(44.dp)
                    .fillMaxHeight()
                    .background(DarkSurfaceDim)
                    .verticalScroll(verticalScrollState)
                    .padding(vertical = 16.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.End,
        ) {
            for (i in 1..lineCount) {
                Text(
                    text = "$i",
                    style = MonoCode.copy(fontSize = 13.sp, lineHeight = 20.sp, color = DarkOutlineVariant),
                )
            }
        }

        // Gutter Divider
        Box(
            modifier =
                Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(DarkOutlineVariant),
        )

        // Code Content Area
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState)
                    .padding(16.dp),
        ) {
            if (isJson) {
                Text(
                    text = highlightJson(text),
                    style = MonoCode.copy(fontSize = 13.sp, lineHeight = 20.sp),
                )
            } else {
                Text(
                    text = text,
                    style = MonoCode.copy(fontSize = 13.sp, lineHeight = 20.sp, color = DarkOnSurface),
                )
            }
        }
    }
}
