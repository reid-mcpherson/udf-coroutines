package com.example.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.ui.navigation.Destination
import com.example.compose.ui.navigation.MainDestination
import com.example.compose.ui.theme.UDFCoroutinesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UDFCoroutinesTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(bottomBar = {
        BottomNavigation {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            Destination.allDestinations.forEach { screen ->
                BottomNavigationItem(
                    icon = {
                        screen.Icon()
                    },
                    label = {
                        Text(text = screen.route)
                    },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            navController.popBackStack()
                        }
                    })
            }
        }
    }) {
        NavHost(
            navController = navController,
            startDestination = MainDestination.DownloadDestination.route
        ) {
            Destination.allDestinations.forEach { destination ->
                composable(destination.route, destination.arguments, destination.deepLinks) {
                    destination(navController, it)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    UDFCoroutinesTheme {
        MainScreen()
    }
}