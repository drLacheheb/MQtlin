package io.github.drlacheheb.mqtlin.ui.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import io.github.drlacheheb.mqtlin.ui.connection.ConnectionComponent
import io.github.drlacheheb.mqtlin.ui.workspace.WorkspaceComponent

interface RootComponent {
    val childStack: Value<ChildStack<*, RootChild>>

    sealed interface RootChild {
        class Connection(val component: ConnectionComponent) : RootChild
        class Workspace(val component: WorkspaceComponent) : RootChild
    }
}

