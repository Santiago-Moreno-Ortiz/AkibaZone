package com.example.projectjuansantiagoaby.presentation.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.projectjuansantiagoaby.domain.model.AnimeDetail
import com.example.projectjuansantiagoaby.ui.components.GenreChip
import com.example.projectjuansantiagoaby.ui.theme.*

@Composable
fun AnimeDetailScreen(
    animeId: String,
    viewModel: AnimeDetailViewModel,
    onBackClick: () -> Unit,
    onPlayClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(animeId) {
        viewModel.loadAnimeDetail(animeId)
    }

    Scaffold(
        containerColor = Background
    ) { padding ->
        when (val state = uiState) {
            is DetailUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Primary) }
            is DetailUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = Error) }
            is DetailUiState.Success -> DetailContent(state.detail, padding, onBackClick, onPlayClick)
        }
    }
}

@Composable
fun DetailContent(
    detail: AnimeDetail,
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onPlayClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(modifier = Modifier.height(300.dp).fillMaxWidth()) {
                AsyncImage(
                    model = detail.anime.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Background))
                    )
                )
                IconButton(onClick = onBackClick, modifier = Modifier.padding(16.dp).align(Alignment.TopStart).background(Color.Black.copy(0.4f), RoundedCornerShape(50))) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(detail.anime.title, style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
                detail.japaneseTitle?.let { Text(it, style = MaterialTheme.typography.titleMedium, color = TextSecondary) }
                
                Spacer(Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = { 
                            detail.episodes.firstOrNull()?.let { onPlayClick(it.id) }
                        }, 
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Ver ahora")
                    }
                    Spacer(Modifier.width(16.dp))
                    IconButton(onClick = { 
                        android.widget.Toast.makeText(context, "Añadido a Favoritos", android.widget.Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.FavoriteBorder, null, tint = Favorite)
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                Text(detail.anime.description ?: "Sin descripción disponible.", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                
                Spacer(Modifier.height(24.dp))
                Text("Episodios", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            }
        }

        items(detail.episodes) { episode ->
            ListItem(
                headlineContent = { Text("Episodio ${episode.number}", color = TextPrimary) },
                leadingContent = { Text(episode.number.padStart(2, '0'), color = Primary, fontWeight = FontWeight.Bold) },
                modifier = Modifier
                    .background(Background)
                    .clickable { onPlayClick(episode.id) }
                    .padding(horizontal = 8.dp),
                colors = ListItemDefaults.colors(containerColor = Background)
            )
            HorizontalDivider(color = BackgroundSecondary, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}
