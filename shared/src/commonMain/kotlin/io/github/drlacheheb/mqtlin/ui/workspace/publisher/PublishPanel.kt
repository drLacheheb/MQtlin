package io.github.drlacheheb.mqtlin.ui.workspace.publisher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.util.JsonUtils
import io.github.drlacheheb.mqtlin.ui.components.MqtlinTextField
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.UiLabelBold
import kotlinx.coroutines.delay

/**
 * Publisher drawer panel allowing users to publish MQTT messages with customizable payload, QoS, and retain flags.
 */
@Composable
fun PublishPanel(
    selectedTopic: String?,
    onPublishMessage: (topic: String, payload: String, qos: Int, isRetained: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isPublishing: Boolean = false,
    publishError: String? = null,
) {
    var topic by remember { mutableStateOf(selectedTopic ?: "home/living-room/temperature") }
    var payload by remember { mutableStateOf("{\n  \"device_id\": \"LR_TEMP_01\",\n  \"command\": \"CALIBRATE\",\n  \"value\": 1.5\n}") }
    var qos by remember { mutableIntStateOf(1) }
    var isRetained by remember { mutableStateOf(false) }
    var showSuccessIndicator by remember { mutableStateOf(false) }

    // Synchronize topic when selectedTopic from tree changes
    LaunchedEffect(selectedTopic) {
        if (!selectedTopic.isNullOrBlank()) {
            topic = selectedTopic
        }
    }

    // Auto-dismiss success indicator after 2.5 seconds
    LaunchedEffect(showSuccessIndicator) {
        if (showSuccessIndicator) {
            delay(2500)
            showSuccessIndicator = false
        }
    }

    // Real-time JSON validation
    val jsonSyntaxError = remember(payload) { JsonUtils.validate(payload) }

    Column(
        modifier =
            modifier
                .fillMaxHeight()
                .background(DarkSurfaceContainer),
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 1. Topic Field Input
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Topic", style = UiLabelBold.copy(fontSize = 12.sp, color = DarkOnSurfaceVariant))
                MqtlinTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    height = 36.dp,
                    isMonospace = true,
                    backgroundColor = DarkSurfaceDim,
                )
            }

            // 2. Syntax-highlighted Payload Editor
            PublishPayloadEditor(
                payload = payload,
                onPayloadChange = { payload = it },
                jsonSyntaxError = jsonSyntaxError,
                modifier = Modifier.weight(1f),
            )

            // 3. QoS, Retain Settings & Publish Trigger Button
            PublishControlsBar(
                qos = qos,
                onQosChange = { qos = it },
                isRetained = isRetained,
                onRetainedChange = { isRetained = it },
                isPublishing = isPublishing,
                showSuccessIndicator = showSuccessIndicator,
                publishError = publishError,
                canPublish = topic.isNotBlank(),
                onPublishClick = {
                    val sanitizedTopic = topic.trim().removePrefix("/")
                    onPublishMessage(sanitizedTopic, payload, qos, isRetained)
                    showSuccessIndicator = true
                },
            )
        }
    }
}
