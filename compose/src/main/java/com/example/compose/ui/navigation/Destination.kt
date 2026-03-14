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
import com.example.compose.ui.screens.Screen3
import com.example.compose.ui.screens.Screen3Feature
import com.example.compose.ui.screens.address.AddressFeature
import com.example.compose.ui.screens.address.AddressScreen
import com.example.compose.ui.screens.download.DownloadFeature
import com.example.compose.ui.screens.download.DownloadScreen

sealed interface Destination {
    companion object {
        val allDestinations =
            listOf(
                MainDestination.DownloadDestination,
                MainDestination.AddressDestination,
                MainDestination.Screen3Destination,
            )
    }

    val route: String

    val arguments: List<NamedNavArgument> get() = emptyList()

    val deepLinks: List<NavDeepLink> get() = emptyList()

    @Composable
    operator fun invoke(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
    )
}

sealed class MainDestination(
    val Icon: @Composable () -> Unit,
) : Destination {
    object DownloadDestination :
        MainDestination(Icon = { Icon(Icons.Filled.Favorite, contentDescription = "screen 1") }) {
        override val route: String = "downloads"

        @Composable
        override operator fun invoke(
            navController: NavController,
            navBackStackEntry: NavBackStackEntry,
        ) {
            DownloadScreen<DownloadFeature>()
        }
    }

    object AddressDestination :
        MainDestination(Icon = { Icon(Icons.Filled.Face, contentDescription = "address") }) {
        override val route: String = "address"

        @Composable
        override operator fun invoke(
            navController: NavController,
            navBackStackEntry: NavBackStackEntry,
        ) {
            AddressScreen<AddressFeature>()
        }
    }

    object Screen3Destination : MainDestination(Icon = {
        Icon(Icons.Filled.Info, contentDescription = "screen 3")
    }) {
        override val route: String = "screen3"

        @Composable
        override operator fun invoke(
            navController: NavController,
            navBackStackEntry: NavBackStackEntry,
        ) {
            Screen3<Screen3Feature>()
        }
    }
}
