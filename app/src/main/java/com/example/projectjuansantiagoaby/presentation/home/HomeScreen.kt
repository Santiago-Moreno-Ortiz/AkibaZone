package com.example.projectjuansantiagoaby.presentation.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.projectjuansantiagoaby.domain.model.Anime
import com.example.projectjuansantiagoaby.domain.usecase.HomeData
import com.example.projectjuansantiagoaby.presentation.state.UiState
import com.example.projectjuansantiagoaby.ui.components.AnimeCard
import com.example.projectjuansantiagoaby.ui.components.GenreChip
import com.example.projectjuansantiagoaby.ui.components.SectionTitle
import com.example.projectjuansantiagoaby.ui.theme.Background
import com.example.projectjuansantiagoaby.ui.theme.BackgroundSecondary
import com.example.projectjuansantiagoaby.ui.theme.Primary
import com.example.projectjuansantiagoaby.ui.theme.TextPrimary
import com.example.projectjuansantiagoaby.ui.theme.TextSecondary
import com.example.projectjuansantiagoaby.ui.theme.Error as ThemeError

val CATALOG_GENRES = listOf(
    "Todos", "Action", "Adventure", "Comedy", "Drama", 
    "Fantasy", "Horror", "Romance", "Sci-Fi", "Slice of Life", "Sports"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAnimeClick: (String) -> Unit,
    onExploreClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val catalogList by viewModel.catalogList.collectAsState()
    val isCatalogLoading by viewModel.isCatalogLoading.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        when (val state = uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(state.message) { viewModel.loadHomeData() }
            is UiState.Success -> HomeContent(
                data = state.data,
                selectedGenre = selectedGenre,
                catalogList = catalogList,
                isCatalogLoading = isCatalogLoading,
                onGenreSelected = { genre -> viewModel.selectGenre(genre) },
                onAnimeClick = onAnimeClick,
                onExploreClick = onExploreClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    data: HomeData,
    selectedGenre: String,
    catalogList: List<Anime>,
    isCatalogLoading: Boolean,
    onGenreSelected: (String) -> Unit,
    onAnimeClick: (String) -> Unit,
    onExploreClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Hero Card (Destacado / Tendencia Principal)
        if (data.trending.isNotEmpty()) {
            item {
                HeroAnimeCard(
                    anime = data.trending.first(),
                    onClick = { onAnimeClick(data.trending.first().link) }
                )
            }
        }

        // Tendencias (Trending)
        if (data.trending.isNotEmpty()) {
            item {
                SectionTitle("🔥 Tendencias del Momento")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(data.trending.drop(1)) { anime ->
                        AnimeCard(anime = anime, onClick = { onAnimeClick(anime.link) })
                    }
                }
            }
        }

        // 📚 CATÁLOGO DE ANIME (Sección Principal en el Inicio)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📚 Catálogo de Anime",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                TextButton(onClick = onExploreClick) {
                    Text("Ver todo →", color = Primary, fontWeight = FontWeight.Bold)
                }
            }

            // Chips de filtro por géneros en el Home
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(CATALOG_GENRES) { genre ->
                    val isSelected = selectedGenre == genre
                    FilterChip(
                        selected = isSelected,
                        onClick = { onGenreSelected(genre) },
                        label = { Text(genre) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White,
                            containerColor = BackgroundSecondary,
                            labelColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        // Lista / Grid del Catálogo en el Home
        if (isCatalogLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary, modifier = Modifier.size(36.dp))
                }
            }
        } else if (catalogList.isNotEmpty()) {
            items(catalogList.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { anime ->
                        AnimeCard(
                            anime = anime,
                            onClick = { onAnimeClick(anime.link) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron animes en esta categoría", color = TextSecondary)
                }
            }
        }

        // Últimos Estrenos
        if (data.latest.isNotEmpty()) {
            item {
                SectionTitle("🆕 En Emisión / Últimos Estrenos")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(data.latest) { anime ->
                        AnimeCard(anime = anime, onClick = { onAnimeClick(anime.link) })
                    }
                }
            }
        }

        // Mejor Valorados
        if (data.topRated.isNotEmpty()) {
            item {
                SectionTitle("⭐ Mejor Valorados")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(data.topRated) { anime ->
                        AnimeCard(anime = anime, onClick = { onAnimeClick(anime.link) })
                    }
                }
            }
        }

        // Recomendados (Populares)
        if (data.popular.isNotEmpty()) {
            item {
                SectionTitle("💡 Recomendados para ti")
            }

            items(data.popular.chunked(2)) { pair ->
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
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
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
            .height(380.dp)
            .background(BackgroundSecondary)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = anime.bannerUrl ?: anime.imageUrl,
            contentDescription = anime.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay for smooth dark blending
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Background.copy(alpha = 0.5f),
                            Background
                        ),
                        startY = 50f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            if (anime.type != null) {
                Surface(
                    color = Primary,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = anime.type.uppercase(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = anime.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                maxLines = 2
            )

            if (anime.genres.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    anime.genres.take(3).forEach { genre ->
                        GenreChip(genre)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ver detalles", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Añadido a favoritos", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TextSecondary)
                ) {
                    Text("Añadir", color = TextPrimary)
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
        Text(message, color = ThemeError)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}
