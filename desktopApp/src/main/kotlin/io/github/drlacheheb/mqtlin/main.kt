package io.github.drlacheheb.mqtlin

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.drlacheheb.mqtlin.data.repository.HiveMqRepository
import io.github.drlacheheb.mqtlin.ui.root.DefaultRootComponent

fun main() {
    val lifecycle = LifecycleRegistry()
    val repository = HiveMqRepository()

    val rootContext = DefaultComponentContext(lifecycle = lifecycle)
    val rootComponent = DefaultRootComponent(
        componentContext = rootContext,
        mqttRepository = repository
    )

    val windowState = WindowState(size = DpSize(1280.dp, 800.dp))

    singleWindowApplication(
        title = "Mqtlin - MQTT Explorer",
        state = windowState
    ) {
        LifecycleController(lifecycle, windowState)
        App(rootComponent = rootComponent)
    }
}