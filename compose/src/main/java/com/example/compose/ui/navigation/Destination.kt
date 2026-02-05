package com.example.compose.ui.navigation

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import com.example.compose.ui.navigation.MainDestination.*
import com.example.compose.ui.screens.Screen2
import com.example.compose.ui.screens.Screen2Feature
import com.example.compose.ui.screens.Screen3
import com.example.compose.ui.screens.Screen3Feature
import com.example.compose.ui.screens.download.DownloadScreen
import com.example.compose.ui.screens.download.DownloadFeature

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

sealed class MainDestination(val Icon: @Composable () -> Unit) : Destination {
    object DownloadDestination :
        MainDestination(Icon = { Icon(Icons.Filled.Favorite, contentDescription = "screen 1") }) {
        override val route: String = "downloads"

        @Composable
        override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
            DownloadScreen.Content<DownloadFeature>()
        }
    }

    object Screen2Destination :
        MainDestination(Icon = { Icon(Icons.Filled.Face, contentDescription = "screen 2") }) {
        override val route: String = "screen2"

        @Composable
        override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
            Screen2.Content<Screen2Feature>()
        }
    }

    object Screen3Destination : MainDestination(Icon = {
        Icon(Icons.Filled.Info, contentDescription = "screen 3")
    }) {
        override val route: String = "screen3"

        @Composable
        override fun Content(navController: NavController, navBackStackEntry: NavBackStackEntry) {
            Screen3.Content<Screen3Feature>()
        }
    }
}





