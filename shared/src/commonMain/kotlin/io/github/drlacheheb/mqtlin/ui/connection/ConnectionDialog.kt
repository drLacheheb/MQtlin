package io.github.drlacheheb.mqtlin.ui.connection

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.domain.model.MqttProtocolVersion
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.domain.usecase.ValidationResult
import io.github.drlacheheb.mqtlin.ui.theme.DarkBackground
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutlineVariant
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainer
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceContainerLow
import io.github.drlacheheb.mqtlin.ui.theme.DarkSurfaceDim
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinError
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinErrorContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinOnError
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinOnPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimary
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinPrimaryContainer
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinSecondary

@Composable
fun ConnectionDialog(
    component: ConnectionComponent,
    onDismissRequest: () -> Unit = {}
) {
    val state by component.state.subscribeAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground.copy(alpha = 0.85f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(820.dp)
                    .height(580.dp),
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceContainerLow,
                border = BorderStroke(1.dp, DarkOutline),
                shadowElevation = 24.dp
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Pane: Saved Profiles
                    SavedProfilesSidebar(
                        modifier = Modifier
                            .width(280.dp)
                            .fillMaxHeight(),
                        activeName = state.name
                    )

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(DarkOutline)
                    )

                    // Right Pane: Profile Configuration Form
                    ProfileConfigForm(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        state = state,
                        component = component,
                        onCancel = onDismissRequest
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedProfilesSidebar(
    modifier: Modifier = Modifier,
    activeName: String
) {
    Column(
        modifier = modifier
            .background(DarkSurfaceContainer)
    ) {
        // Search Profiles
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search profiles...", fontSize = 13.sp, color = DarkOnSurfaceVariant.copy(alpha = 0.5f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = DarkOnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(6.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MqtlinPrimary,
                    unfocusedBorderColor = DarkOutline,
                    focusedContainerColor = DarkSurfaceDim,
                    unfocusedContainerColor = DarkSurfaceDim
                ),
                singleLine = true
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkOutline)
        )

        // Profile List
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ProfileListItem(
                title = "Local Mosquitto",
                subtitle = "127.0.0.1:1883",
                isActive = true,
                badge = null
            )
            ProfileListItem(
                title = "AWS IoT Core",
                subtitle = "Production",
                isActive = false,
                badge = "TLS"
            )
            ProfileListItem(
                title = "Home Assistant MQTT",
                subtitle = "192.168.1.100:1883",
                isActive = false,
                badge = null
            )
            ProfileListItem(
                title = "EMQX Cloud Staging",
                subtitle = "staging.emqx.cloud",
                isActive = false,
                badge = "WSS"
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkOutline)
        )

        // New Profile Button
        Box(modifier = Modifier.padding(12.dp)) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, DarkOutline),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DarkOnSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Profile", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ProfileListItem(
    title: String,
    subtitle: String,
    isActive: Boolean,
    badge: String?
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        color = if (isActive) MqtlinPrimaryContainer.copy(alpha = 0.2f) else Color.Transparent,
        border = if (isActive) BorderStroke(1.dp, MqtlinPrimary.copy(alpha = 0.4f)) else null
    ) {
        Row(
            modifier = Modifier
                .clickable { }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(if (isActive) pulseScale else 1f)
                    .background(
                        color = if (isActive) MqtlinSecondary else DarkOutlineVariant,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = DarkOnSurface
                    )

                    if (badge != null) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MqtlinPrimary.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, MqtlinPrimary.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MqtlinPrimary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = DarkOnSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ProfileConfigForm(
    modifier: Modifier = Modifier,
    state: ConnectionUiState,
    component: ConnectionComponent,
    onCancel: () -> Unit
) {
    Column(
        modifier = modifier
            .background(DarkSurfaceContainerLow)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Connection Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkOnSurface
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = state.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkOnSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile Name",
                    tint = DarkOnSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkOutline)
        )

        // Form Fields Container
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Error Banner if connection failed
            if (state.connectionState is ConnectionState.Error) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    color = MqtlinErrorContainer.copy(alpha = 0.25f),
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

            // Host & Port Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Host Input
                Column(modifier = Modifier.weight(0.7f)) {
                    Text(
                        text = "HOST / IP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkOnSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = state.host,
                        onValueChange = component::onHostChanged,
                        isError = state.validationErrors.containsKey(ValidationResult.Field.HOST),
                        placeholder = { Text("e.g. 127.0.0.1 or broker.emqx.io", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MqtlinPrimary,
                            unfocusedBorderColor = DarkOutline,
                            focusedContainerColor = DarkSurfaceDim,
                            unfocusedContainerColor = DarkSurfaceDim,
                            focusedTextColor = DarkOnSurface,
                            unfocusedTextColor = DarkOnSurface
                        ),
                        singleLine = true
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

                // Port Input
                Column(modifier = Modifier.weight(0.3f)) {
                    Text(
                        text = "PORT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkOnSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = state.portText,
                        onValueChange = component::onPortChanged,
                        isError = state.validationErrors.containsKey(ValidationResult.Field.PORT),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MqtlinPrimary,
                            unfocusedBorderColor = DarkOutline,
                            focusedContainerColor = DarkSurfaceDim,
                            unfocusedContainerColor = DarkSurfaceDim,
                            focusedTextColor = DarkOnSurface,
                            unfocusedTextColor = DarkOnSurface
                        ),
                        singleLine = true
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
                Text(
                    text = "CLIENT ID",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkOnSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.clientId,
                    onValueChange = component::onClientIdChanged,
                    isError = state.validationErrors.containsKey(ValidationResult.Field.CLIENT_ID),
                    trailingIcon = {
                        IconButton(onClick = component::onGenerateRandomClientId) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = "Generate Random ID",
                                tint = DarkOnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MqtlinPrimary,
                        unfocusedBorderColor = DarkOutline,
                        focusedContainerColor = DarkSurfaceDim,
                        unfocusedContainerColor = DarkSurfaceDim,
                        focusedTextColor = DarkOnSurface,
                        unfocusedTextColor = DarkOnSurface
                    ),
                    singleLine = true
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Protocol Version
                Column(modifier = Modifier.weight(0.5f)) {
                    Text(
                        text = "PROTOCOL VERSION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkOnSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceDim, RoundedCornerShape(6.dp))
                            .border(1.dp, DarkOutline, RoundedCornerShape(6.dp))
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
                        text = "TRANSPORT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkOnSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceDim, RoundedCornerShape(6.dp))
                            .border(1.dp, DarkOutline, RoundedCornerShape(6.dp))
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(0.5f)) {
                    Text(
                        text = "USERNAME (OPTIONAL)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkOnSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = state.username,
                        onValueChange = component::onUsernameChanged,
                        placeholder = { Text("Username", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MqtlinPrimary,
                            unfocusedBorderColor = DarkOutline,
                            focusedContainerColor = DarkSurfaceDim,
                            unfocusedContainerColor = DarkSurfaceDim,
                            focusedTextColor = DarkOnSurface,
                            unfocusedTextColor = DarkOnSurface
                        ),
                        singleLine = true
                    )
                }

                Column(modifier = Modifier.weight(0.5f)) {
                    Text(
                        text = "PASSWORD (OPTIONAL)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkOnSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = component::onPasswordChanged,
                        placeholder = { Text("Password", fontSize = 13.sp) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MqtlinPrimary,
                            unfocusedBorderColor = DarkOutline,
                            focusedContainerColor = DarkSurfaceDim,
                            unfocusedContainerColor = DarkSurfaceDim,
                            focusedTextColor = DarkOnSurface,
                            unfocusedTextColor = DarkOnSurface
                        ),
                        singleLine = true
                    )
                }
            }
        }

        // Footer Actions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkOutline)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceContainer)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onCancel,
                colors = ButtonDefaults.textButtonColors(contentColor = DarkOnSurfaceVariant)
            ) {
                Text("Cancel", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.connectionState is ConnectionState.Connected) {
                    Button(
                        onClick = component::onDisconnectClicked,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MqtlinError,
                            contentColor = MqtlinOnError
                        )
                    ) {
                        Text("Disconnect", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = component::onConnectClicked,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, DarkOutline),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkOnSurface)
                    ) {
                        Text("Test Connection", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = component::onConnectClicked,
                        enabled = state.connectionState !is ConnectionState.Connecting,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MqtlinPrimary,
                            contentColor = MqtlinOnPrimary
                        )
                    ) {
                        if (state.connectionState is ConnectionState.Connecting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MqtlinOnPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Connecting...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Save & Connect", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProtocolPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(32.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(4.dp),
        color = if (isSelected) MqtlinPrimaryContainer else Color.Transparent
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MqtlinPrimary else DarkOnSurfaceVariant
            )
        }
    }
}
