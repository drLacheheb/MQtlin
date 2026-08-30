package io.github.drlacheheb.mqtlin.ui.connection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.domain.usecase.ValidationResult
import io.github.drlacheheb.mqtlin.ui.components.MqtlinTextField
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorder
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineMuted
import io.github.drlacheheb.mqtlin.ui.theme.InputBackground
import io.github.drlacheheb.mqtlin.ui.theme.LabelXs
import io.github.drlacheheb.mqtlin.ui.theme.ModalSurface
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinError

/**
 * Profile Configuration Form for editing MQTT broker connection parameters.
 */
@Composable
fun ProfileConfigForm(
    state: ConnectionUiState,
    component: ConnectionComponent,
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(ModalSurface)
    ) {
        // 1. Breadcrumb Title Header & Window Controls
        ConnectionFormHeader(
            profileName = state.name,
            onNameChange = component::onNameChanged
        )

        // 2. Main Form Fields (Scrollable area)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Status Feedback Banners
            if (state.testSuccessMessage != null) {
                ConnectionSuccessBanner(message = state.testSuccessMessage)
            } else if (state.connectionState is ConnectionState.Error) {
                ConnectionErrorBanner(message = state.connectionState.message)
            }

            // Host & Port Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(2f)) {
                    Text(text = "Host / IP", style = LabelXs)
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Port", style = LabelXs)
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

            // Client ID Row
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Client ID", style = LabelXs)
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

            // Protocol Version & Transport Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Protocol Version
                Column(modifier = Modifier.weight(0.5f)) {
                    Text(text = "Protocol Version", style = LabelXs)
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
                    Text(text = "Transport", style = LabelXs)
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
                    Text(text = "Username (Optional)", style = LabelXs)
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
                    Text(text = "Password (Optional)", style = LabelXs)
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

        // 3. Action Footer (Cancel, Disconnect, Test Connection, Connect/Save)
        ConnectionFormFooter(
            state = state,
            onCancel = onCancel,
            onDisconnect = component::onDisconnectClicked,
            onTestConnection = component::onTestConnectionClicked,
            onConnect = component::onConnectClicked
        )
    }
}
