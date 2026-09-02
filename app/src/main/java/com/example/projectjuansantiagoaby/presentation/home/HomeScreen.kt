package com.example.projectjuansantiagoaby.presentation.home

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.projectjuansantiagoaby.domain.model.Anime
import com.example.projectjuansantiagoaby.ui.components.AnimeCard
import com.example.projectjuansantiagoaby.ui.components.SectionTitle
import com.example.projectjuansantiagoaby.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAnimeClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { AkibaZoneHeader(onSearchClick, onProfileClick) },
        containerColor = Background
    ) { padding ->
        when (val state = uiState) {
            is HomeUiState.Loading -> LoadingState()
            is HomeUiState.Error -> ErrorState(state.message) { viewModel.loadHomeData() }
            is HomeUiState.Success -> HomeContent(state, padding, onAnimeClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AkibaZoneHeader(onSearchClick: () -> Unit, onProfileClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "AkibaZone",
                color = Primary,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextPrimary)
            }
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, contentDescription = "Perfil", tint = TextPrimary)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Background
        )
    )
}

@Composable
fun HomeContent(
    state: HomeUiState.Success,
    padding: PaddingValues,
    onAnimeClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        // Hero Card (Featured Anime)
        if (state.popularAnime.isNotEmpty()) {
            item {
                HeroAnimeCard(anime = state.popularAnime.first(), onClick = { onAnimeClick(state.popularAnime.first().link) })
            }
        }

        // Últimos Estrenos
        if (state.latestReleases.isNotEmpty()) {
            item {
                SectionTitle("Últimos Estrenos")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.latestReleases) { anime ->
                        AnimeCard(anime = anime, onClick = { onAnimeClick(anime.link) })
                    }
                }
            }
        }

        // Trending
        if (state.trending.isNotEmpty()) {
            item {
                SectionTitle("Tendencias")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.trending) { anime ->
                        AnimeCard(anime = anime, onClick = { onAnimeClick(anime.link) })
                    }
                }
            }
        }

        // Recomendados (Populares)
        item {
            SectionTitle("Recomendados para ti")
        }
        
        items(state.popularAnime.drop(1).chunked(2)) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pair.forEach { anime ->
                    AnimeCard(
                        anime = anime, 
                        onClick = { onAnimeClick(anime.link) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun HeroAnimeCard(anime: Anime, onClick: () -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(BackgroundSecondary)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = anime.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Background),
                        startY = 100f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Text(
                text = anime.title,
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Ver ahora", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                OutlinedButton(
                    onClick = { 
                        Toast.makeText(context, "Añadido a Mi Lista", Toast.LENGTH_SHORT).show()
                    },
                    border = androidx.compose.foundation.BorderStroke(1.dp, TextSecondary)
                ) {
                    Text("Mi lista", color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Primary)
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, color = Error)
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}
