package com.example.compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import com.example.compose.DownloadScreen
import com.example.compose.Screen2
import com.example.compose.Screen3

sealed interface Destination {

    companion object {
        val allDestinations = listOf(
            DownloadDestination,
            Screen2Destination,
            Screen3Destination,
        )
    }

    val route: String

    val arguments: List<NamedNavArgument> get() = emptyList()

    val deepLinks: List<NavDeepLink> get() = emptyList()

    @Composable
    fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry
    )
}

object DownloadDestination : Destination {
    override val route: String = "downloads"

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        DownloadScreen.Content(navBackStackEntry.arguments)
    }
}

object Screen2Destination : Destination {
    override val route: String = "screen2"

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        Screen2.Content(navBackStackEntry.arguments)
    }
}

object Screen3Destination : Destination {
    override val route: String = "screen3"

    @Composable
    override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
        Screen3.Content(navBackStackEntry.arguments)
    }

}



