package com.example.projectjuansantiagoaby.presentation.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.projectjuansantiagoaby.ui.components.AnimeCard
import com.example.projectjuansantiagoaby.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onAnimeClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Background).padding(16.dp)) {
        TextField(
            value = query,
            onValueChange = { 
                query = it
                if (it.length > 2) viewModel.search(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar anime...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(androidx.compose.material.icons.Icons.Default.Close, null, tint = TextSecondary)
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = BackgroundSecondary,
                unfocusedContainerColor = BackgroundSecondary,
                focusedIndicatorColor = Primary,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Primary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { viewModel.search(query) })
        )
        
        Spacer(Modifier.height(16.dp))
        
        // Chips de géneros (Estaticos por ahora)
        val genres = listOf("Todos", "Acción", "Aventura", "Romance", "Shonen")
        var selectedGenre by remember { mutableStateOf("Todos") }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            genres.forEach { genre ->
                FilterChip(
                    selected = genre == selectedGenre,
                    onClick = { selectedGenre = genre },
                    label = { Text(genre) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor = Color.White,
                        containerColor = BackgroundSecondary,
                        labelColor = TextSecondary
                    )
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        when (val state = uiState) {
            is ExploreUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Primary) }
            is ExploreUiState.Success -> {
                if (state.results.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron resultados", color = TextSecondary)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.results) { anime ->
                            AnimeCard(anime, onClick = { onAnimeClick(anime.link) }, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
            is ExploreUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = Error) }
            is ExploreUiState.Idle -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Busca tus animes favoritos", color = TextSecondary) }
        }
    }
}
