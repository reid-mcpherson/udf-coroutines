package com.example.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.ui.navigation.Destination
import com.example.compose.ui.theme.UDFCoroutinesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate")
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

private sealed class Screen(val route: String, val Icon: @Composable () -> Unit) {
    object HelloWorldDestination :
        Screen("home", Icon = { Icon(Icons.Filled.Favorite, contentDescription = "screen 1") })

    object LoginDestination :
        Screen("screen2", Icon = { Icon(Icons.Filled.Face, contentDescription = "screen 2") })

    object Screen3Destination :
        Screen("screen3", Icon = { Icon(Icons.Filled.Info, contentDescription = "screen 3") })
}


private val navItems = listOf(
    Screen.HelloWorldDestination,
    Screen.LoginDestination,
    Screen.Screen3Destination
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(bottomBar = {
        BottomNavigation {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            navItems.forEach { screen ->
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
            startDestination = Screen.HelloWorldDestination.route
        ) {
            Destination.allDestinations.forEach { destination ->
                composable(destination.route, destination.arguments, destination.deepLinks) {
                    destination.Content(navController, it)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
//    UDFCoroutinesTheme {
//        Greeting("Android")
//    }
}