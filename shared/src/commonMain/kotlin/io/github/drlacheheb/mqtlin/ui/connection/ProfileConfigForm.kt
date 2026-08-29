package io.github.drlacheheb.mqtlin.ui.connection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.domain.usecase.ValidationResult
import io.github.drlacheheb.mqtlin.ui.components.MqtlinDangerButton
import io.github.drlacheheb.mqtlin.ui.components.MqtlinOutlinedButton
import io.github.drlacheheb.mqtlin.ui.components.MqtlinPrimaryButton
import io.github.drlacheheb.mqtlin.ui.components.MqtlinTextButton
import io.github.drlacheheb.mqtlin.ui.components.MqtlinTextField
import io.github.drlacheheb.mqtlin.ui.components.WindowControls
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorder
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorderHover
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineMuted
import io.github.drlacheheb.mqtlin.ui.theme.FooterBackground
import io.github.drlacheheb.mqtlin.ui.theme.InputBackground
import io.github.drlacheheb.mqtlin.ui.theme.LabelXs
import io.github.drlacheheb.mqtlin.ui.theme.ModalSurface
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinError
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinErrorContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinOnPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary

@Composable
fun ProfileConfigForm(
    modifier: Modifier = Modifier,
    state: ConnectionUiState,
    component: ConnectionComponent,
    onCancel: () -> Unit
) {
    Column(
        modifier = modifier.background(ModalSurface)
    ) {
        // Header: Breadcrumb Style (Connection Settings › [Profile Name]) with custom window controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Breadcrumb
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Text(
                    text = "Connection Settings",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkOnSurfaceVariant
                    )
                )

                Text(
                    text = "›",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = DarkOutlineMuted
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                InlineEditableProfileName(
                    name = state.name,
                    onNameChange = component::onNameChanged
                )
            }

            // Right Window Controls
            WindowControls(height = 48)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkBorder)
        )

        // Form Content (p-gutter flex-1 overflow-y-auto)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Success Banner if test connection succeeded
            if (state.testSuccessMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    color = MqtlinSecondary.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, MqtlinSecondary.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = MqtlinSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.testSuccessMessage,
                            fontSize = 12.sp,
                            color = MqtlinSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Error Banner if connection or test failed
            if (state.connectionState is ConnectionState.Error && state.testSuccessMessage == null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    color = MqtlinErrorContainer.copy(alpha = 0.20f),
                    border = BorderStroke(1.dp, MqtlinErrorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = MqtlinError,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = (state.connectionState as ConnectionState.Error).message,
                            fontSize = 12.sp,
                            color = MqtlinError,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Host & Port Row (8 / 4 column grid)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Host Input (col-8)
                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        text = "Host / IP",
                        style = LabelXs
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    MqtlinTextField(
                        value = state.host,
                        onValueChange = component::onHostChanged,
                        isError = state.validationErrors.containsKey(ValidationResult.Field.HOST),
                        placeholder = "127.0.0.1",
                        height = 36.dp,
                        isMonospace = true
                    )
                    if (state.validationErrors.containsKey(ValidationResult.Field.HOST)) {
                        Text(
                            text = state.validationErrors[ValidationResult.Field.HOST] ?: "",
                            fontSize = 11.sp,
                            color = MqtlinError,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Port Input (col-4)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Port",
                        style = LabelXs
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    MqtlinTextField(
                        value = state.portText,
                        onValueChange = component::onPortChanged,
                        isError = state.validationErrors.containsKey(ValidationResult.Field.PORT),
                        placeholder = "1883",
                        height = 36.dp,
                        isMonospace = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    if (state.validationErrors.containsKey(ValidationResult.Field.PORT)) {
                        Text(
                            text = state.validationErrors[ValidationResult.Field.PORT] ?: "",
                            fontSize = 11.sp,
                            color = MqtlinError,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // Client ID Row (w-full with casino dice button)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Client ID",
                    style = LabelXs
                )
                Spacer(modifier = Modifier.height(4.dp))
                MqtlinTextField(
                    value = state.clientId,
                    onValueChange = component::onClientIdChanged,
                    isError = state.validationErrors.containsKey(ValidationResult.Field.CLIENT_ID),
                    height = 36.dp,
                    isMonospace = true,
                    trailingIcon = {
                        IconButton(
                            onClick = component::onGenerateRandomClientId,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = "Generate Random ID",
                                tint = DarkOutlineMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                )
                if (state.validationErrors.containsKey(ValidationResult.Field.CLIENT_ID)) {
                    Text(
                        text = state.validationErrors[ValidationResult.Field.CLIENT_ID] ?: "",
                        fontSize = 11.sp,
                        color = MqtlinError,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Protocol & Transport Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Protocol Version
                Column(modifier = Modifier.weight(0.5f)) {
                    Text(
                        text = "Protocol Version",
                        style = LabelXs
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .background(InputBackground, RoundedCornerShape(4.dp))
                            .border(1.dp, DarkBorder, RoundedCornerShape(4.dp))
                            .padding(2.dp)
                    ) {
                        ProtocolPill(
                            label = "MQTT 5.0",
                            isSelected = state.protocolVersion == MqttProtocolVersion.MQTT_5_0,
                            onClick = { component.onProtocolVersionChanged(MqttProtocolVersion.MQTT_5_0) },
                            modifier = Modifier.weight(1f)
                        )
                        ProtocolPill(
                            label = "MQTT 3.1.1",
                            isSelected = state.protocolVersion == MqttProtocolVersion.MQTT_3_1_1,
                            onClick = { component.onProtocolVersionChanged(MqttProtocolVersion.MQTT_3_1_1) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Transport
                Column(modifier = Modifier.weight(0.5f)) {
                    Text(
                        text = "Transport",
                        style = LabelXs
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .background(InputBackground, RoundedCornerShape(4.dp))
                            .border(1.dp, DarkBorder, RoundedCornerShape(4.dp))
                            .padding(2.dp)
                    ) {
                        ProtocolPill(
                            label = "TCP",
                            isSelected = state.transport == TransportProtocol.TCP,
                            onClick = { component.onTransportChanged(TransportProtocol.TCP) },
                            modifier = Modifier.weight(1f)
                        )
                        ProtocolPill(
                            label = "TLS",
                            isSelected = state.transport == TransportProtocol.TLS,
                            onClick = { component.onTransportChanged(TransportProtocol.TLS) },
                            modifier = Modifier.weight(1f)
                        )
                        ProtocolPill(
                            label = "WS",
                            isSelected = state.transport == TransportProtocol.WS,
                            onClick = { component.onTransportChanged(TransportProtocol.WS) },
                            modifier = Modifier.weight(1f)
                        )
                        ProtocolPill(
                            label = "WSS",
                            isSelected = state.transport == TransportProtocol.WSS,
                            onClick = { component.onTransportChanged(TransportProtocol.WSS) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Username & Password Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(0.5f)) {
                    Text(
                        text = "Username (Optional)",
                        style = LabelXs
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    MqtlinTextField(
                        value = state.username,
                        onValueChange = component::onUsernameChanged,
                        placeholder = "Username",
                        height = 36.dp,
                        isMonospace = false
                    )
                }

                Column(modifier = Modifier.weight(0.5f)) {
                    Text(
                        text = "Password (Optional)",
                        style = LabelXs
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    MqtlinTextField(
                        value = state.password,
                        onValueChange = component::onPasswordChanged,
                        placeholder = "Password",
                        height = 36.dp,
                        isMonospace = false,
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            }
        }

        // Footer Actions: px-gutter py-panel_padding border-t border-[#27272a] bg-[#18181b]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkBorder)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FooterBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MqtlinTextButton(
                onClick = onCancel
            ) {
                Text("Cancel")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.connectionState is ConnectionState.Connected && !state.isTesting) {
                    MqtlinDangerButton(
                        onClick = component::onDisconnectClicked
                    ) {
                        Text("Disconnect")
                    }
                } else {
                    MqtlinOutlinedButton(
                        onClick = component::onTestConnectionClicked,
                        enabled = !state.isTesting && state.connectionState !is ConnectionState.Connecting
                    ) {
                        if (state.isTesting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = DarkOnSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Testing...")
                        } else {
                            Text("Test Connection")
                        }
                    }

                    MqtlinPrimaryButton(
                        onClick = component::onConnectClicked,
                        enabled = !state.isTesting && state.connectionState !is ConnectionState.Connecting
                    ) {
                        if (state.connectionState is ConnectionState.Connecting && !state.isTesting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = MqtlinOnPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Connecting...")
                        } else {
                            Text("Save & Connect")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InlineEditableProfileName(
    name: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .widthIn(min = 60.dp, max = 240.dp)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> isHovered = true
                            PointerEventType.Exit -> isHovered = false
                        }
                    }
                }
            }
            .drawBehind {
                val strokeColor = when {
                    isFocused -> MqtlinPrimary
                    isHovered -> DarkBorderHover
                    else -> Color.Transparent
                }
                if (strokeColor != Color.Transparent) {
                    drawLine(
                        color = strokeColor,
                        start = Offset(0f, size.height - 1f),
                        end = Offset(size.width, size.height - 1f),
                        strokeWidth = if (isFocused) 1.5.dp.toPx() else 1.dp.toPx()
                    )
                }
            }
            .padding(horizontal = 2.dp, vertical = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = name,
            onValueChange = { input ->
                onNameChange(input.take(MAX_PROFILE_NAME_LENGTH))
            },
            textStyle = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkOnSurface,
                textAlign = TextAlign.Start
            ),
            singleLine = true,
            cursorBrush = SolidColor(MqtlinPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused }
        )
    }
}
