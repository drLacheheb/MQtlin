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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.drlacheheb.mqtlin.domain.model.ConnectionConfig
import io.github.drlacheheb.mqtlin.domain.model.TransportProtocol
import io.github.drlacheheb.mqtlin.ui.components.MqtlinOutlinedButton
import io.github.drlacheheb.mqtlin.ui.components.MqtlinTextField
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorder
import io.github.drlacheheb.mqtlin.ui.theme.DarkOnSurfaceVariant
import io.github.drlacheheb.mqtlin.ui.theme.SidebarBackground

@Composable
fun SavedProfilesSidebar(
    modifier: Modifier = Modifier,
    profiles: List<ConnectionConfig>,
    searchQuery: String,
    activeName: String,
    onSearchQueryChanged: (String) -> Unit = {},
    onProfileSelected: (ConnectionConfig) -> Unit = {},
    onNewProfileClicked: () -> Unit = {}
) {
    val filteredProfiles = if (searchQuery.isBlank()) {
        profiles
    } else {
        profiles.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.host.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier.background(SidebarBackground)
    ) {
        // Search Profiles Bar (aligned with 48.dp header)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 12.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            MqtlinTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
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

        // Dynamic Profile List or Empty State
        if (filteredProfiles.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        tint = DarkOnSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = if (searchQuery.isNotBlank()) "No matching profiles" else "No saved profiles yet",
                        fontSize = 13.sp,
                        color = DarkOnSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (searchQuery.isNotBlank()) "Try a different search term" else "Connect or click New Profile below to save",
                        fontSize = 11.sp,
                        color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                filteredProfiles.forEach { profile ->
                    val isActive = activeName == profile.name
                    val badge = when (profile.transport) {
                        TransportProtocol.TLS -> "TLS"
                        TransportProtocol.WSS -> "WSS"
                        TransportProtocol.WS -> "WS"
                        TransportProtocol.TCP -> null
                    }

                    ProfileListItem(
                        title = profile.name,
                        subtitle = "${profile.host}:${profile.port}",
                        isActive = isActive,
                        badge = badge,
                        onClick = { onProfileSelected(profile) }
                    )
                }
            }
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
