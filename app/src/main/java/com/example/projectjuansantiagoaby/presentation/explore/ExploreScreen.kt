package com.example.projectjuansantiagoaby.presentation.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectjuansantiagoaby.presentation.state.UiState
import com.example.projectjuansantiagoaby.ui.components.AnimeCard
import com.example.projectjuansantiagoaby.ui.theme.*

val GENRES_LIST = listOf(
    "Action", "Adventure", "Comedy", "Drama", "Fantasy", 
    "Horror", "Mahou Shoujo", "Mecha", "Music", "Mystery", 
    "Psychological", "Romance", "Sci-Fi", "Slice of Life", 
    "Sports", "Supernatural", "Thriller"
)

val FORMATS_MAP = mapOf(
    "TV" to "TV",
    "Película" to "MOVIE",
    "OVA" to "OVA",
    "Especial" to "SPECIAL",
    "ONA" to "ONA"
)

val SORTS_MAP = mapOf(
    "Más Populares" to "POPULARITY_DESC",
    "En Tendencia" to "TRENDING_DESC",
    "Mejor Valorados" to "SCORE_DESC",
    "Más Recientes" to "START_DATE_DESC"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onAnimeClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    var showFiltersSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        // Search Bar & Filter Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = filterState.query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Buscar anime por título...", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                trailingIcon = {
                    if (filterState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Close, null, tint = TextSecondary)
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
                keyboardActions = KeyboardActions(onSearch = { viewModel.applyFilters() })
            )

            Spacer(Modifier.width(8.dp))

            FilterIconButton(
                hasActiveFilters = filterState.selectedGenre != null || filterState.selectedFormat != null || filterState.selectedSort != "POPULARITY_DESC",
                onClick = { showFiltersSheet = true }
            )
        }

        Spacer(Modifier.height(12.dp))

        // Quick Genre Bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(GENRES_LIST) { genre ->
                val isSelected = filterState.selectedGenre == genre
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.onGenreSelected(genre) },
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

        Spacer(Modifier.height(12.dp))

        // Active filters indicators if any
        if (filterState.selectedGenre != null || filterState.selectedFormat != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Filtros:", color = TextSecondary, fontSize = 12.sp)
                
                filterState.selectedGenre?.let { genre ->
                    AssistChip(
                        onClick = { viewModel.onGenreSelected(genre) },
                        label = { Text(genre, fontSize = 12.sp) },
                        trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp)) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Primary.copy(alpha = 0.2f), labelColor = Primary)
                    )
                }

                filterState.selectedFormat?.let { format ->
                    val displayFormat = FORMATS_MAP.entries.firstOrNull { it.value == format }?.key ?: format
                    AssistChip(
                        onClick = { viewModel.onFormatSelected(format) },
                        label = { Text(displayFormat, fontSize = 12.sp) },
                        trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp)) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Primary.copy(alpha = 0.2f), labelColor = Primary)
                    )
                }

                TextButton(onClick = { viewModel.clearFilters() }) {
                    Text("Limpiar", fontSize = 12.sp, color = Error)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // Content Grid
        when (val state = uiState) {
            is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No se encontraron animes con los filtros seleccionados", color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.clearFilters() }, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                                Text("Restablecer Filtros")
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.data) { anime ->
                            AnimeCard(
                                anime = anime,
                                onClick = { onAnimeClick(anime.link) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, color = Error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.applyFilters() }) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Filters
    if (showFiltersSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFiltersSheet = false },
            containerColor = BackgroundSecondary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filtros de Búsqueda", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    TextButton(onClick = { viewModel.clearFilters() }) {
                        Text("Limpiar todo", color = Error)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Ordenar por
                Text("Ordenar por", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SORTS_MAP.toList()) { (label, value) ->
                        FilterChip(
                            selected = filterState.selectedSort == value,
                            onClick = { viewModel.onSortSelected(value) },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Tipo / Formato
                Text("Formato", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(FORMATS_MAP.toList()) { (label, value) ->
                        FilterChip(
                            selected = filterState.selectedFormat == value,
                            onClick = { viewModel.onFormatSelected(value) },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { showFiltersSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Aplicar Filtros", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FilterIconButton(hasActiveFilters: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .background(
                if (hasActiveFilters) Primary else BackgroundSecondary,
                shape = RoundedCornerShape(12.dp)
            )
            .size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filtros",
            tint = if (hasActiveFilters) Color.White else TextPrimary
        )
    }
}
