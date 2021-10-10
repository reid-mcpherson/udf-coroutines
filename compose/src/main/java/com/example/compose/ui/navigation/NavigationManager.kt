package com.example.compose.ui.navigation

import android.os.Bundle
import androidx.navigation.NamedNavArgument
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NavigationManager {

    private val commandFlow = MutableSharedFlow<NavigationCommand>(1)

    val commands = commandFlow.asSharedFlow()

    fun navigate(command: NavigationCommand) {
        commandFlow.tryEmit(command)
    }
}

interface NavigationCommand {

    val arguments: NavigationArguments

    val destination: String
}

data class NavigationArguments(
    val namedNavArguments: List<NamedNavArgument> = emptyList(),
    val params: Bundle = Bundle.EMPTY,
    val popBackStack: Boolean = false
)