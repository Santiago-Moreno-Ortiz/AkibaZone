package com.example.projectjuansantiagoaby

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.projectjuansantiagoaby.navigation.Screen
import com.example.projectjuansantiagoaby.presentation.MainViewModelFactory
import com.example.projectjuansantiagoaby.presentation.anime.AnimeDetailScreen
import com.example.projectjuansantiagoaby.presentation.anime.AnimeDetailViewModel
import com.example.projectjuansantiagoaby.presentation.home.HomeScreen
import com.example.projectjuansantiagoaby.presentation.home.HomeViewModel
import com.example.projectjuansantiagoaby.ui.theme.AkibaZoneTheme
import com.example.projectjuansantiagoaby.ui.theme.Background
import com.example.projectjuansantiagoaby.ui.theme.Primary
import com.example.projectjuansantiagoaby.ui.theme.TextSecondary
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AkibaZoneTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.Explore,
        Screen.Favorites,
        Screen.History,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Background,
                contentColor = Primary
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = Background
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController, 
            startDestination = Screen.Home.route, 
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = viewModel(factory = MainViewModelFactory(androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application))
                HomeScreen(
                    viewModel = viewModel,
                    onAnimeClick = { animeId -> 
                        val encodedId = URLEncoder.encode(animeId, StandardCharsets.UTF_8.toString())
                        navController.navigate(Screen.Detail.createRoute(encodedId)) 
                    },
                    onSearchClick = { navController.navigate(Screen.Explore.route) },
                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                )
            }
            composable(Screen.Explore.route) {
                val viewModel: com.example.projectjuansantiagoaby.presentation.explore.ExploreViewModel = viewModel(factory = MainViewModelFactory(androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application))
                com.example.projectjuansantiagoaby.presentation.explore.ExploreScreen(
                    viewModel = viewModel,
                    onAnimeClick = { animeId -> 
                        val encodedId = URLEncoder.encode(animeId, StandardCharsets.UTF_8.toString())
                        navController.navigate(Screen.Detail.createRoute(encodedId)) 
                    }
                )
            }
            composable(Screen.Favorites.route) { PlaceholderScreen("Favoritos") }
            composable(Screen.History.route) { PlaceholderScreen("Historial") }
            composable(Screen.Profile.route) { PlaceholderScreen("Perfil") }
            composable(Screen.Detail.route) { backStackEntry ->
                val animeId = backStackEntry.arguments?.getString("animeId") ?: ""
                val viewModel: AnimeDetailViewModel = viewModel(factory = MainViewModelFactory(androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application))
                AnimeDetailScreen(
                    animeId = animeId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onPlayClick = { episodeId -> 
                        val encodedEp = URLEncoder.encode(episodeId, StandardCharsets.UTF_8.toString())
                        navController.navigate(Screen.Player.createRoute(encodedEp)) 
                    }
                )
            }
            composable(Screen.Player.route) { backStackEntry ->
                val episodeId = backStackEntry.arguments?.getString("episodeId") ?: ""
                val viewModel: com.example.projectjuansantiagoaby.presentation.player.PlayerViewModel = viewModel(factory = MainViewModelFactory(androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application))
                com.example.projectjuansantiagoaby.presentation.player.PlayerScreen(
                    episodeId = episodeId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = Background) {
        Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(text = name, color = com.example.projectjuansantiagoaby.ui.theme.TextPrimary, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
