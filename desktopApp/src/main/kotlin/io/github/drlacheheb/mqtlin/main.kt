package io.github.drlacheheb.mqtlin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.drlacheheb.mqtlin.data.repository.FileProfileRepository
import io.github.drlacheheb.mqtlin.data.repository.HiveMqRepository
import io.github.drlacheheb.mqtlin.ui.components.LocalWindowActions
import io.github.drlacheheb.mqtlin.ui.components.WindowActions
import io.github.drlacheheb.mqtlin.ui.root.DefaultRootComponent
import io.github.drlacheheb.mqtlin.ui.root.RootComponent
import io.github.drlacheheb.mqtlin.ui.theme.DarkBackground
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorder

fun main() = application {
    val lifecycle = LifecycleRegistry()
    val repository = HiveMqRepository()
    val profileRepository = FileProfileRepository()

    val rootContext = DefaultComponentContext(lifecycle = lifecycle)
    val rootComponent = DefaultRootComponent(
        componentContext = rootContext,
        mqttRepository = repository,
        profileRepository = profileRepository
    )

    val windowState = rememberWindowState(
        size = DpSize(800.dp, 560.dp),
        position = WindowPosition.Aligned(Alignment.Center)
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "MQtlin",
        icon = painterResource("icons/icon.png"),
        undecorated = true,
        resizable = true
    ) {
        val childStack by rootComponent.childStack.subscribeAsState()
        val isWorkspace = childStack.active.instance is RootComponent.RootChild.Workspace

        // Dynamically adjust window size and position between Connection Manager and Workspace
        LaunchedEffect(isWorkspace) {
            if (isWorkspace) {
                windowState.size = DpSize(1280.dp, 800.dp)
                windowState.position = WindowPosition.Aligned(Alignment.Center)
            } else {
                windowState.size = DpSize(800.dp, 560.dp)
                windowState.position = WindowPosition.Aligned(Alignment.Center)
            }
        }

        LifecycleController(lifecycle, windowState)

        val windowActions = WindowActions(
            onMinimize = { windowState.isMinimized = true },
            onMaximizeRestore = {
                windowState.placement = if (windowState.placement == WindowPlacement.Maximized) {
                    WindowPlacement.Floating
                } else {
                    WindowPlacement.Maximized
                }
            },
            onClose = { exitApplication() },
            isMaximized = windowState.placement == WindowPlacement.Maximized
        )

        CompositionLocalProvider(LocalWindowActions provides windowActions) {
            WindowDraggableArea {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBackground)
                        .border(1.dp, DarkBorder)
                ) {
                    App(rootComponent = rootComponent)
                }
            }
        }
    }
}