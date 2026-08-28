package io.github.drlacheheb.mqtlin.ui.connection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import io.github.drlacheheb.mqtlin.ui.theme.CanvasBackground
import io.github.drlacheheb.mqtlin.ui.theme.DarkOutline
import io.github.drlacheheb.mqtlin.ui.theme.ModalSurface

@Composable
fun ConnectionDialog(
    component: ConnectionComponent,
    onDismissRequest: () -> Unit = {}
) {
    val state by component.state.subscribeAsState()

    // Modal Backdrop: rgba(9, 9, 11, 0.85)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CanvasBackground.copy(alpha = 0.85f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Modal Container: w-[800px] h-[560px] bg-[#121215] border border-[#27272a] rounded-xl shadow-2xl
            Surface(
                modifier = Modifier
                    .width(800.dp)
                    .height(560.dp),
                shape = RoundedCornerShape(12.dp),
                color = ModalSurface,
                border = BorderStroke(1.dp, DarkOutline),
                shadowElevation = 32.dp
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Pane: Saved Profiles (w-[280px] bg-[#18181b] border-r border-[#27272a])
                    SavedProfilesSidebar(
                        modifier = Modifier
                            .width(280.dp)
                            .fillMaxHeight(),
                        activeName = state.name,
                        onProfileSelected = { component.onNameChanged(it) }
                    )

                    // Vertical Divider border-[#27272a]
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(DarkOutline)
                    )

                    // Right Pane: Profile Configuration Form (flex-1 bg-[#121215])
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
