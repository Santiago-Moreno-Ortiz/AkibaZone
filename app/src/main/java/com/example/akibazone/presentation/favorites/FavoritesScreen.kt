package com.example.akibazone.presentation.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.akibazone.presentation.state.UiState
import com.example.akibazone.ui.components.AnimeCard
import com.example.akibazone.ui.theme.Background
import com.example.akibazone.ui.theme.Primary
import com.example.akibazone.ui.theme.TextSecondary

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onAnimeClick: (String) -> Unit
) {
    val uiState by viewModel.favoritesState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Background).padding(16.dp)) {
        Text(
            text = "Mis Favoritos",
            style = MaterialTheme.typography.headlineMedium,
            color = com.example.akibazone.ui.theme.TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (val state = uiState) {
            is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Primary) }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aún no tienes favoritos", color = TextSecondary)
                    }
                } else {
                    LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.data) { anime ->
                            AnimeCard(anime, onClick = { onAnimeClick(anime.link) })
                        }
                    }
                }
            }
            is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = com.example.akibazone.ui.theme.Error) }
        }
    }
}
