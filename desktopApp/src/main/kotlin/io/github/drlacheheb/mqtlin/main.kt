package io.github.drlacheheb.mqtlin

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.drlacheheb.mqtlin.data.repository.HiveMqRepository
import io.github.drlacheheb.mqtlin.domain.usecase.ValidateConnectionConfigUseCase
import io.github.drlacheheb.mqtlin.ui.connection.DefaultConnectionComponent

fun main() {
    val lifecycle = LifecycleRegistry()
    val repository = HiveMqRepository()
    val validateUseCase = ValidateConnectionConfigUseCase()

    val rootContext = DefaultComponentContext(lifecycle = lifecycle)
    val connectionComponent = DefaultConnectionComponent(
        componentContext = rootContext,
        mqttRepository = repository,
        validateConfigUseCase = validateUseCase
    )

    val windowState = WindowState(size = DpSize(1280.dp, 800.dp))

    singleWindowApplication(
        title = "Mqtlin - MQTT Explorer",
        state = windowState
    ) {
        LifecycleController(lifecycle, windowState)
        App(connectionComponent = connectionComponent)
    }
}