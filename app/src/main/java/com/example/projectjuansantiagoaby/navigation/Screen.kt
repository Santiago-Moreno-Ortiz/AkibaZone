package com.example.projectjuansantiagoaby.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Explore : Screen("explore", "Explorar", Icons.Default.Explore)
    object Favorites : Screen("favorites", "Favoritos", Icons.Default.Favorite)
    object History : Screen("history", "Historial", Icons.Default.History)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
    
    object Detail : Screen("detail/{animeId}", "Detalle") {
        fun createRoute(animeId: String) = "detail/$animeId"
    }
    
    object Player : Screen("player/{episodeId}", "Reproductor") {
        fun createRoute(episodeId: String) = "player/$episodeId"
    }
    
    object Settings : Screen("settings", "Configuración")
}
