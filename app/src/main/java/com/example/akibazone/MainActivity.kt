package com.example.akibazone

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.Rational
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.akibazone.navigation.Screen
import com.example.akibazone.presentation.MainViewModelFactory
import com.example.akibazone.presentation.anime.AnimeDetailScreen
import com.example.akibazone.presentation.anime.AnimeDetailViewModel
import com.example.akibazone.presentation.home.HomeScreen
import com.example.akibazone.presentation.home.HomeViewModel
import com.example.akibazone.presentation.history.HistoryScreen
import com.example.akibazone.presentation.history.HistoryViewModel
import com.example.akibazone.presentation.profile.ProfileViewModel
import com.example.akibazone.presentation.settings.SettingsScreen
import com.example.akibazone.presentation.settings.SettingsViewModel
import com.example.akibazone.ui.theme.AkibaZoneTheme
import com.example.akibazone.ui.theme.Background
import com.example.akibazone.ui.theme.Primary
import com.example.akibazone.ui.theme.TextPrimary
import com.example.akibazone.ui.theme.TextSecondary

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private var playerScreenActive = false
    private var playbackAllowsPip = false
    private var pipMode by mutableStateOf(false)

    private fun supportsPip() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pipParams(): PictureInPictureParams {
        val builder = PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setAutoEnterEnabled(playerScreenActive && playbackAllowsPip)
        }
        return builder.build()
    }

    private fun updatePipParams() {
        if (supportsPip() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setPictureInPictureParams(pipParams())
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S && supportsPip() &&
            playerScreenActive && playbackAllowsPip && !isInPictureInPictureMode
        ) {
            enterPictureInPictureMode(pipParams())
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        pipMode = isInPictureInPictureMode
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val application = applicationContext as android.app.Application
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = MainViewModelFactory(application)
            )
            val themePreference by settingsViewModel.themePreference.collectAsState()
            AkibaZoneTheme(themePreference = themePreference) {
                MainApp(
                    settingsViewModel = settingsViewModel,
                    isInPip = pipMode,
                    onPlayerScreenChanged = { active ->
                        playerScreenActive = active
                        if (!active) playbackAllowsPip = false
                        updatePipParams()
                    },
                    onPipEligibilityChanged = { allowed ->
                        playbackAllowsPip = allowed
                        updatePipParams()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    settingsViewModel: SettingsViewModel,
    isInPip: Boolean,
    onPlayerScreenChanged: (Boolean) -> Unit,
    onPipEligibilityChanged: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val reportPlayerScreen by rememberUpdatedState(onPlayerScreenChanged)
    DisposableEffect(navController) {
        val listener = androidx.navigation.NavController.OnDestinationChangedListener { _, destination, _ ->
            reportPlayerScreen(destination.route == Screen.Player.route || destination.route == Screen.DemoPlayer.route)
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
            reportPlayerScreen(false)
        }
    }

    val items = listOf(
        Screen.Home,
        Screen.Explore,
        Screen.Favorites,
        Screen.History,
        Screen.Profile
    )

    fun navigateTo(route: String) {
        navController.navigate(route) {
            // Popping up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    Scaffold(
        contentWindowInsets = if (isInPip) WindowInsets(0, 0, 0, 0) else ScaffoldDefaults.contentWindowInsets,
        topBar = {
            if (currentRoute != Screen.Player.route && currentRoute != Screen.DemoPlayer.route) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "AkibaZone",
                            color = Primary,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            modifier = Modifier.clickable {
                                navigateTo(Screen.Home.route)
                            }
                        )
                    },
                    navigationIcon = {
                        if (currentRoute != Screen.Home.route && !items.any { it.route == currentRoute }) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = TextPrimary)
                            }
                        }
                    },
                    actions = {
                        if (currentRoute == Screen.Home.route) {
                            IconButton(onClick = { navigateTo(Screen.Explore.route) }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextPrimary)
                            }
                        }
                        IconButton(onClick = { navigateTo(Screen.Profile.route) }) {
                            Icon(Icons.Default.Person, contentDescription = "Perfil", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Background
                    )
                )
            }
        },
        bottomBar = {
            if (currentRoute != Screen.Player.route && currentRoute != Screen.DemoPlayer.route) {
                NavigationBar(
                    containerColor = Background,
                    contentColor = Primary
                ) {
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
                            onClick = { navigateTo(screen.route) }
                        )
                    }
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
                        navController.navigate(Screen.Detail.createRoute(animeId))
                    },
                    onExploreClick = {
                        navigateTo(Screen.Explore.route)
                    }
                )
            }
            composable(Screen.Explore.route) {
                val viewModel: com.example.akibazone.presentation.explore.ExploreViewModel = viewModel(factory = MainViewModelFactory(androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application))
                com.example.akibazone.presentation.explore.ExploreScreen(
                    viewModel = viewModel,
                    onAnimeClick = { animeId ->
                        navController.navigate(Screen.Detail.createRoute(animeId))
                    }
                )
            }
            composable(Screen.Favorites.route) {
                val viewModel: com.example.akibazone.presentation.favorites.FavoritesViewModel = viewModel(factory = MainViewModelFactory(androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application))
                com.example.akibazone.presentation.favorites.FavoritesScreen(
                    viewModel = viewModel,
                    onAnimeClick = { animeId ->
                        navController.navigate(Screen.Detail.createRoute(animeId))
                    }
                )
            }
            composable(Screen.History.route) {
                val viewModel: HistoryViewModel = viewModel(
                    factory = MainViewModelFactory(
                        androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
                    )
                )
                HistoryScreen(
                    viewModel = viewModel,
                    onAnimeClick = { animeId ->
                        navController.navigate(Screen.Detail.createRoute(animeId))
                    }
                )
            }
            composable(Screen.Profile.route) {
                val application = androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
                val viewModel: com.example.akibazone.presentation.profile.AuthViewModel = viewModel(factory = MainViewModelFactory(application))
                val profileViewModel: ProfileViewModel = viewModel(factory = MainViewModelFactory(application))
                com.example.akibazone.presentation.profile.ProfileScreen(
                    viewModel = viewModel,
                    profileViewModel = profileViewModel,
                    onFavoritesClick = { navigateTo(Screen.Favorites.route) },
                    onHistoryClick = { navigateTo(Screen.History.route) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onDemoClick = { navController.navigate(Screen.DemoPlayer.route) }
                )
            }
            composable(Screen.Detail.route) { backStackEntry ->
                val animeId = backStackEntry.arguments?.getString("animeId") ?: ""
                val viewModel: AnimeDetailViewModel = viewModel(factory = MainViewModelFactory(androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application))
                AnimeDetailScreen(
                    animeId = animeId,
                    viewModel = viewModel,
                    onPlayClick = { episodeId ->
                        navController.navigate(Screen.Player.createRoute(episodeId))
                    }
                )
            }
            composable(Screen.DemoPlayer.route) {
                val viewModel = viewModel { com.example.akibazone.presentation.player.PlayerViewModel() }
                com.example.akibazone.presentation.player.PlayerScreen(
                    input = com.example.akibazone.presentation.player.PlayerInput.Demo,
                    viewModel = viewModel,
                    isInPip = isInPip,
                    onPipEligibilityChanged = onPipEligibilityChanged,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.Player.route) { backStackEntry ->
                val episodeId = backStackEntry.arguments?.getString("episodeId") ?: ""
                val viewModel: com.example.akibazone.presentation.player.PlayerViewModel = viewModel(factory = MainViewModelFactory(androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application))
                com.example.akibazone.presentation.player.PlayerScreen(
                    input = com.example.akibazone.presentation.player.PlayerInput.Episode(episodeId),
                    isInPip = isInPip,
                    onPipEligibilityChanged = onPipEligibilityChanged,
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
            Text(text = name, color = com.example.akibazone.ui.theme.TextPrimary, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
