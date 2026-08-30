package io.github.drlacheheb.mqtlin.ui

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import io.github.drlacheheb.mqtlin.fakes.FakeMqttRepository
import io.github.drlacheheb.mqtlin.ui.root.DefaultRootComponent
import io.github.drlacheheb.mqtlin.ui.root.RootComponent
import io.github.drlacheheb.mqtlin.ui.settings.DefaultSettingsComponent
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsComponentTest {

    @Test
    fun `default settings component initial state is under construction and close triggers callback`() = runTest {
        val lifecycle = LifecycleRegistry()
        lifecycle.resume()
        var dismissed = false
        val context = DefaultComponentContext(lifecycle = lifecycle)
        val component = DefaultSettingsComponent(
            componentContext = context,
            onDismiss = { dismissed = true }
        )

        component.state.value.isUnderConstruction shouldBe true
        component.state.value.title shouldBe "MQtlin Settings"

        component.onClose()
        dismissed shouldBe true

        lifecycle.destroy()
    }

    @Test
    fun `opening settings in RootComponent activates dialogSlot without closing active workspace or connection`() = runTest {
        val lifecycle = LifecycleRegistry()
        lifecycle.resume()
        val context = DefaultComponentContext(lifecycle = lifecycle)
        val mqttRepo = FakeMqttRepository()
        val testDispatcher = StandardTestDispatcher(testScheduler)

        val rootComponent = DefaultRootComponent(
            componentContext = context,
            mqttRepository = mqttRepo,
            mainContext = testDispatcher
        )

        // Initially no dialog
        rootComponent.dialogSlot.value.child.shouldBeNull()
        rootComponent.childStack.value.active.instance.shouldBeInstanceOf<RootComponent.RootChild.Connection>()

        // Open settings
        rootComponent.onOpenSettings()
        rootComponent.dialogSlot.value.child.shouldNotBeNull()
        rootComponent.dialogSlot.value.child?.instance.shouldBeInstanceOf<RootComponent.DialogChild.Settings>()

        // Active screen remains untouched
        rootComponent.childStack.value.active.instance.shouldBeInstanceOf<RootComponent.RootChild.Connection>()

        // Dismiss settings
        rootComponent.onDismissDialog()
        rootComponent.dialogSlot.value.child.shouldBeNull()

        lifecycle.destroy()
    }
}
