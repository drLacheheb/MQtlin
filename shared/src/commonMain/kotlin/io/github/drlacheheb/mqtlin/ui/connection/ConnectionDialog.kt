package io.github.drlacheheb.mqtlin.ui.connection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorder
import io.github.drlacheheb.mqtlin.ui.theme.ModalSurface

@Composable
fun ConnectionDialog(
    component: ConnectionComponent,
    onCancel: () -> Unit = {}
) {
    val state by component.state.subscribeAsState()

    // Full Window Surface (w-[800px] h-[560px] bg-[#121215])
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ModalSurface
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Pane: Saved Profiles (w-[280px] bg-[#18181b] border-r border-[#27272a])
            SavedProfilesSidebar(
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight(),
                profiles = state.savedProfiles,
                searchQuery = state.profileSearchQuery,
                activeName = state.name,
                onSearchQueryChanged = component::onProfileSearchQueryChanged,
                onProfileSelected = component::onProfileSelected,
                onNewProfileClicked = component::onNewProfileClicked
            )

            // Vertical Divider border-[#27272a]
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(DarkBorder)
            )

            // Right Pane: Profile Configuration Form (flex-1 bg-[#121215])
            ProfileConfigForm(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                state = state,
                component = component,
                onCancel = onCancel
            )
        }
    }
}
