package io.github.drlacheheb.mqtlin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import io.github.drlacheheb.mqtlin.ui.connection.ConnectionDialog
import io.github.drlacheheb.mqtlin.ui.root.DefaultRootComponent
import io.github.drlacheheb.mqtlin.ui.root.RootComponent
import io.github.drlacheheb.mqtlin.ui.settings.SettingsScreen
import io.github.drlacheheb.mqtlin.ui.theme.DarkBackground
import io.github.drlacheheb.mqtlin.ui.theme.DarkBorder
import io.github.drlacheheb.mqtlin.ui.theme.MqtlinTheme
import javax.swing.SwingUtilities

fun <T> runOnUiThread(block: () -> T): T {
    if (SwingUtilities.isEventDispatchThread()) {
        return block()
    }
    var error: Throwable? = null
    var result: T? = null
    SwingUtilities.invokeAndWait {
        try {
            result = block()
        } catch (e: Throwable) {
            error = e
        }
    }
    error?.also { throw it }
    @Suppress("UNCHECKED_CAST")
    return result as T
}

fun main() {
    val lifecycle = LifecycleRegistry()
    val repository = HiveMqRepository()
    val profileRepository = FileProfileRepository()

    val rootComponent = runOnUiThread {
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            mqttRepository = repository,
            profileRepository = profileRepository
        )
    }

    application {
        val windowState = rememberWindowState(
            size = DpSize(800.dp, 560.dp),
            position = WindowPosition.Aligned(Alignment.Center)
        )

        // Primary Window (Connection Manager / Workspace)
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

        // Auxiliary Multi-Windows (Settings, Connection Switcher)
        val dialogSlot by rootComponent.dialogSlot.subscribeAsState()

        when (val dialogChild = dialogSlot.child?.instance) {
            is RootComponent.DialogChild.Settings -> {
                val settingsWindowState = rememberWindowState(
                    size = DpSize(520.dp, 380.dp),
                    position = WindowPosition.Aligned(Alignment.Center)
                )

                Window(
                    onCloseRequest = rootComponent::onDismissDialog,
                    state = settingsWindowState,
                    title = "MQtlin Settings",
                    icon = painterResource("icons/icon.png"),
                    undecorated = true,
                    resizable = false
                ) {
                    MqtlinTheme {
                        WindowDraggableArea {
                            SettingsScreen(
                                component = dialogChild.component,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
            is RootComponent.DialogChild.ConnectionManager -> {
                val connectionWindowState = rememberWindowState(
                    size = DpSize(800.dp, 560.dp),
                    position = WindowPosition.Aligned(Alignment.Center)
                )

                val dialogWindowActions = WindowActions(
                    onMinimize = { connectionWindowState.isMinimized = true },
                    onMaximizeRestore = {
                        connectionWindowState.placement = if (connectionWindowState.placement == WindowPlacement.Maximized) {
                            WindowPlacement.Floating
                        } else {
                            WindowPlacement.Maximized
                        }
                    },
                    onClose = { rootComponent.onDismissDialog() },
                    isMaximized = connectionWindowState.placement == WindowPlacement.Maximized
                )

                Window(
                    onCloseRequest = rootComponent::onDismissDialog,
                    state = connectionWindowState,
                    title = "MQtlin - Switch Connection",
                    icon = painterResource("icons/icon.png"),
                    undecorated = true,
                    resizable = true
                ) {
                    CompositionLocalProvider(LocalWindowActions provides dialogWindowActions) {
                        MqtlinTheme {
                            WindowDraggableArea {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(DarkBackground)
                                        .border(1.dp, DarkBorder)
                                ) {
                                    ConnectionDialog(
                                        component = dialogChild.component,
                                        onCancel = rootComponent::onDismissDialog
                                    )
                                }
                            }
                        }
                    }
                }
            }
            null -> {}
        }
    }
}
