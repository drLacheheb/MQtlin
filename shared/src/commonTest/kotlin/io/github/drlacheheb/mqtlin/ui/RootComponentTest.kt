package io.github.drlacheheb.mqtlin.ui

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import io.github.drlacheheb.mqtlin.fakes.FakeMqttRepository
import io.github.drlacheheb.mqtlin.ui.root.DefaultRootComponent
import io.github.drlacheheb.mqtlin.ui.root.RootComponent
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
        val component =
            DefaultRootComponent(
                componentContext = context,
                mqttRepository = fakeRepository,
                mainContext = StandardTestDispatcher(testScope.testScheduler),
            )
        return Pair(component, lifecycle)
    }

    @Test
    fun `initial state displays ConnectionDialog child`() =
        runTest {
            val (root, lifecycle) = createRootComponent(this)

            val activeChild = root.childStack.value.active.instance
            activeChild.shouldBeInstanceOf<RootComponent.RootChild.Connection>()

            lifecycle.destroy()
        }

    @Test
    fun `connecting successfully navigates to WorkspaceScreen child`() =
        runTest {
            val (root, lifecycle) = createRootComponent(this)

            val connectionChild = root.childStack.value.active.instance as RootComponent.RootChild.Connection
            connectionChild.component.onConnectClicked()
            advanceUntilIdle()

            val activeChild = root.childStack.value.active.instance
            activeChild.shouldBeInstanceOf<RootComponent.RootChild.Workspace>()

            lifecycle.destroy()
        }

    @Test
    fun `onOpenConnectionManager activates DialogChild ConnectionManager without closing workspace`() =
        runTest {
            val (root, lifecycle) = createRootComponent(this)

            // 1. Connect to initial workspace
            val connectionChild = root.childStack.value.active.instance as RootComponent.RootChild.Connection
            connectionChild.component.onConnectClicked()
            advanceUntilIdle()

            root.childStack.value.active.instance
                .shouldBeInstanceOf<RootComponent.RootChild.Workspace>()
            root.dialogSlot.value.child
                .shouldBeNull()

            // 2. Open Connection Manager from workspace
            root.onOpenConnectionManager()

            root.dialogSlot.value.child
                .shouldNotBeNull()
            root.dialogSlot.value.child
                ?.instance
                .shouldBeInstanceOf<RootComponent.DialogChild.ConnectionManager>()
            // Workspace remains active in background
            root.childStack.value.active.instance
                .shouldBeInstanceOf<RootComponent.RootChild.Workspace>()

            // 3. Dismiss dialog
            root.onDismissDialog()
            root.dialogSlot.value.child
                .shouldBeNull()
            root.childStack.value.active.instance
                .shouldBeInstanceOf<RootComponent.RootChild.Workspace>()

            lifecycle.destroy()
        }

    @Test
    fun `connecting inside ConnectionManager dialog dismisses dialog and transitions to new workspace`() =
        runTest {
            val (root, lifecycle) = createRootComponent(this)

            // 1. Connect to initial workspace
            val connectionChild = root.childStack.value.active.instance as RootComponent.RootChild.Connection
            connectionChild.component.onConnectClicked()
            advanceUntilIdle()

            // 2. Open Connection Manager dialog
            root.onOpenConnectionManager()
            val dialogInstance =
                root.dialogSlot.value.child
                    ?.instance as RootComponent.DialogChild.ConnectionManager

            // 3. Connect to second broker
            dialogInstance.component.onHostChanged("broker.emqx.io")
            dialogInstance.component.onConnectClicked()
            advanceUntilIdle()

            // Dialog should now be closed and active workspace updated
            root.dialogSlot.value.child
                .shouldBeNull()
            val activeWorkspace = root.childStack.value.active.instance as RootComponent.RootChild.Workspace
            activeWorkspace.component.state.value.connectionConfig
                ?.host shouldBe "broker.emqx.io"

            lifecycle.destroy()
        }
}
