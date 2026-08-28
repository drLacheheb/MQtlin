package io.github.drlacheheb.mqtlin.ui

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import io.github.drlacheheb.mqtlin.fakes.FakeMqttRepository
import io.github.drlacheheb.mqtlin.ui.root.DefaultRootComponent
import io.github.drlacheheb.mqtlin.ui.root.RootComponent
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RootComponentTest {

    private val fakeRepository = FakeMqttRepository()

    private fun createRootComponent(testScope: TestScope): Pair<DefaultRootComponent, LifecycleRegistry> {
        val lifecycle = LifecycleRegistry()
        lifecycle.resume()
        val context = DefaultComponentContext(lifecycle = lifecycle)
        val component = DefaultRootComponent(
            componentContext = context,
            mqttRepository = fakeRepository,
            mainContext = StandardTestDispatcher(testScope.testScheduler)
        )
        return Pair(component, lifecycle)
    }

    @Test
    fun `initial state displays ConnectionDialog child`() = runTest {
        val (root, lifecycle) = createRootComponent(this)

        val activeChild = root.childStack.value.active.instance
        activeChild.shouldBeInstanceOf<RootComponent.RootChild.Connection>()

        lifecycle.destroy()
    }

    @Test
    fun `connecting successfully navigates to WorkspaceScreen child`() = runTest {
        val (root, lifecycle) = createRootComponent(this)

        val connectionChild = root.childStack.value.active.instance as RootComponent.RootChild.Connection
        connectionChild.component.onConnectClicked()
        advanceUntilIdle()

        val activeChild = root.childStack.value.active.instance
        activeChild.shouldBeInstanceOf<RootComponent.RootChild.Workspace>()

        lifecycle.destroy()
    }
}

