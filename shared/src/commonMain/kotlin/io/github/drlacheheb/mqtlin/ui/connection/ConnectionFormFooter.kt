package io.github.drlacheheb.mqtlin.ui.connection

import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.drlacheheb.mqtlin.domain.model.ConnectionState
import io.github.drlacheheb.mqtlin.ui.components.MqtlinDangerButton
import io.github.drlacheheb.mqtlin.ui.components.MqtlinOutlinedButton
import io.github.drlacheheb.mqtlin.ui.components.MqtlinPrimaryButton
import io.github.drlacheheb.mqtlin.ui.components.MqtlinTextButton
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorder
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurface
import io.github.drlacheheb.mqtlin.ui.theme.FooterBackground
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinOnPrimary

/**
 * Bottom action footer of the Connection Dialog (Cancel, Disconnect, Test Connection, Save & Connect).
 */
@Composable
fun ConnectionFormFooter(
    state: ConnectionUiState,
    onCancel: () -> Unit,
    onDisconnect: () -> Unit,
    onTestConnection: () -> Unit,
    onConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
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
                        onClick = onDisconnect
                    ) {
                        Text("Disconnect")
                    }
                } else {
                    MqtlinOutlinedButton(
                        onClick = onTestConnection,
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
                        onClick = onConnect,
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
