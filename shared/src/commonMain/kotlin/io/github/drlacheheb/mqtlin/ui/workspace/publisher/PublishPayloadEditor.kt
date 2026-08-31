package io.github.drlacheheb.mqtlin.ui.workspace.publisher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.util.JsonUtils
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinError
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinErrorContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold
import io.github.drlacheheb.mqtlin.ui.util.JsonVisualTransformation

/**
 * Multi-line syntax-highlighted payload editor with 1-click format trigger and live JSON error banner.
 */
@Composable
fun PublishPayloadEditor(
    payload: String,
    onPayloadChange: (String) -> Unit,
    jsonSyntaxError: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Payload (JSON)",
                style = UiLabelBold.copy(fontSize = 12.sp, color = DarkOnSurfaceVariant),
            )
            Text(
                text = "Format",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MqtlinPrimary,
                modifier =
                    Modifier
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable {
                            onPayloadChange(JsonUtils.format(payload))
                        }.padding(2.dp),
            )
        }

        // Textarea Container (with red error border if syntax error)
        val borderColor = if (jsonSyntaxError != null) MqtlinErrorContainer else DarkOutlineVariant
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(DarkSurfaceDim, RoundedCornerShape(4.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(4.dp)),
        ) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(10.dp),
            ) {
                BasicTextField(
                    value = payload,
                    onValueChange = onPayloadChange,
                    textStyle =
                        TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = DarkOnSurface,
                            lineHeight = 18.sp,
                        ),
                    visualTransformation = remember { JsonVisualTransformation() },
                    cursorBrush = SolidColor(MqtlinPrimary),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .pointerHoverIcon(PointerIcon.Text),
                )
            }

            // Validation Error Banner
            if (jsonSyntaxError != null) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(MqtlinErrorContainer.copy(alpha = 0.20f))
                            .border(BorderStroke(1.dp, MqtlinErrorContainer.copy(alpha = 0.50f)))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Syntax Error",
                        tint = MqtlinError,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Syntax Error: $jsonSyntaxError",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MqtlinError,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
