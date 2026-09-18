package com.example.akibazone.presentation.anime

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.akibazone.domain.model.AnimeDetail
import com.example.akibazone.domain.model.Episode
import com.example.akibazone.domain.model.EpisodePlaybackType
import com.example.akibazone.presentation.state.UiState
import com.example.akibazone.ui.components.GenreChip
import com.example.akibazone.ui.theme.*

@Composable
fun AnimeDetailScreen(
    animeId: String,
    viewModel: AnimeDetailViewModel,
    onPlayClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(animeId) {
        viewModel.loadAnimeDetail(animeId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        when (val state = uiState) {
            is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Primary) }
            is UiState.Error -> com.example.akibazone.presentation.home.ErrorState(state.message) { viewModel.loadAnimeDetail(animeId) }
            is UiState.Success -> DetailContent(state.data, onPlayClick, onFavoriteClick = { viewModel.toggleFavorite(state.data.anime) })
        }
    }
}

@Composable
fun DetailContent(
    detail: AnimeDetail,
    onPlayClick: (String) -> Unit,
    onFavoriteClick: () -> Unit
) {
    val context = LocalContext.current
    val openEpisode: (Episode) -> Unit = { episode ->
        if (episode.playbackType == EpisodePlaybackType.EXTERNAL) {
            if (!openExternalEpisode(context, episode.url)) {
                Toast.makeText(
                    context,
                    "No hay una aplicación disponible para abrir este episodio.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            onPlayClick(episode.url ?: episode.id)
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(modifier = Modifier.height(340.dp).fillMaxWidth()) {
                AsyncImage(
                    model = detail.anime.bannerUrl ?: detail.anime.imageUrl,
                    contentDescription = detail.anime.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Background.copy(alpha = 0.7f), Background))
                    )
                )

                // Poster & Title Info Overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    AsyncImage(
                        model = detail.anime.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .width(110.dp)
                            .height(160.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = detail.anime.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            maxLines = 2
                        )

                        detail.japaneseTitle?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = Warning, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${detail.anime.rating} / 10",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(12.dp))
                            detail.format?.let {
                                Surface(color = Primary, shape = RoundedCornerShape(4.dp)) {
                                    Text(it, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                // Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            detail.episodes.firstOrNull()?.let(openEpisode)
                        },
                        enabled = detail.episodes.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        val isExternal = detail.episodes.firstOrNull()?.playbackType == EpisodePlaybackType.EXTERNAL
                        Icon(if (isExternal) Icons.AutoMirrored.Filled.OpenInNew else Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isExternal) "Abrir episodio" else "Reproducir", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.width(12.dp))

                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.background(BackgroundSecondary, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = if (detail.anime.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (detail.anime.isFavorite) com.example.akibazone.ui.theme.Favorite else TextPrimary
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Genres
                if (detail.anime.genres.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(detail.anime.genres) { genre ->
                            GenreChip(genre)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Metadata Details Card
                Surface(
                    color = BackgroundSecondary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        detail.status?.let { InfoColumn("Estado", it) }
                        detail.year?.let { InfoColumn("Año", it) }
                        detail.studio?.let { InfoColumn("Estudio", it) }
                        detail.duration?.let { InfoColumn("Duración", it) }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Sinopsis", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = detail.anime.description ?: "Sin descripción disponible.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(24.dp))
                if (detail.episodes.isEmpty()) {
                    Text("No hay información oficial de episodios disponible.", color = TextSecondary)
                } else {
                    Text("Episodios disponibles", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                }
            }
        }

        items(detail.episodes) { episode ->
            ListItem(
                headlineContent = {
                    Text(
                        episode.title
                            ?: episode.number.takeIf { it.isNotBlank() }?.let { "Episodio $it" }
                            ?: "Episodio disponible",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                supportingContent = episode.site?.takeIf { it.isNotBlank() }?.let { site ->
                    { Text(site, color = TextSecondary) }
                },
                leadingContent = {
                    if (!episode.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = episode.imageUrl,
                            contentDescription = episode.title,
                            modifier = Modifier
                                .width(112.dp)
                                .height(64.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            color = Primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(
                                imageVector = if (episode.playbackType == EpisodePlaybackType.EXTERNAL) Icons.AutoMirrored.Filled.OpenInNew else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                },
                trailingContent = {
                    TextButton(onClick = { openEpisode(episode) }) {
                        Text(if (episode.playbackType == EpisodePlaybackType.EXTERNAL) "Abrir episodio" else "Reproducir")
                    }
                },
                modifier = Modifier
                    .background(Background)
                    .clickable { openEpisode(episode) }
                    .padding(horizontal = 8.dp),
                colors = ListItemDefaults.colors(containerColor = Background)
            )
            HorizontalDivider(color = BackgroundSecondary, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

private fun openExternalEpisode(context: android.content.Context, url: String?): Boolean {
    val uri = url?.trim()?.takeIf { it.isNotEmpty() }?.let(Uri::parse) ?: return false
    if (uri.scheme?.lowercase() !in setOf("http", "https")) return false

    return try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        true
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: SecurityException) {
        false
    }
}

@Composable
fun InfoColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Spacer(Modifier.height(2.dp))
        Text(value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
