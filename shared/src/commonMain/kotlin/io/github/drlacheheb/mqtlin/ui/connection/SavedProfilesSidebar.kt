package io.github.drlacheheb.mqtlin.ui.connection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.ui.components.MqtlinOutlinedButton
import io.github.drlacheheb.mqtlin.ui.components.MqtlinTextField
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorder
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.SidebarBackground

@Composable
fun SavedProfilesSidebar(
    modifier: Modifier = Modifier,
    activeName: String,
    onProfileSelected: (String) -> Unit = {},
    onNewProfileClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier.background(SidebarBackground)
    ) {
        // Search Profiles Bar (p-panel_padding border-b border-[#27272a])
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            MqtlinTextField(
                value = "",
                onValueChange = {},
                placeholder = "Search profiles...",
                height = 34.dp,
                isMonospace = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = DarkOnSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkBorder)
        )

        // Profile List (flex-1 p-tight_gap overflow-y-auto)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ProfileListItem(
                title = "Local Mosquitto",
                subtitle = "127.0.0.1:1883",
                isActive = activeName == "Local Mosquitto",
                badge = null,
                onClick = { onProfileSelected("Local Mosquitto") }
            )
            ProfileListItem(
                title = "AWS IoT Core",
                subtitle = "Production",
                isActive = activeName == "AWS IoT Core",
                badge = "TLS",
                onClick = { onProfileSelected("AWS IoT Core") }
            )
            ProfileListItem(
                title = "Home Assistant MQTT",
                subtitle = "192.168.1.100:1883",
                isActive = activeName == "Home Assistant MQTT",
                badge = null,
                onClick = { onProfileSelected("Home Assistant MQTT") }
            )
            ProfileListItem(
                title = "EMQX Cloud Staging",
                subtitle = "staging.emqx.cloud",
                isActive = activeName == "EMQX Cloud Staging",
                badge = "WSS",
                onClick = { onProfileSelected("EMQX Cloud Staging") }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DarkBorder)
        )

        // New Profile Button (p-panel_padding border-t border-[#27272a])
        Box(modifier = Modifier.padding(12.dp)) {
            MqtlinOutlinedButton(
                onClick = onNewProfileClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Profile")
            }
        }
    }
}
