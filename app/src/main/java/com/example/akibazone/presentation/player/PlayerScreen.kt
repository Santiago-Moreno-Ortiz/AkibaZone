package com.example.akibazone.presentation.player

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    episodeId: String,
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(episodeId) {
        viewModel.loadVideo(episodeId)
    }

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build()
    }

<<<<<<< HEAD:app/src/main/java/com/example/projectjuansantiagoaby/presentation/player/PlayerScreen.kt
    DisposableEffect(exoPlayer) {
=======
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(exoPlayer, lifecycleOwner) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                viewModel.onPlaybackError()
            }
        }
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_STOP) exoPlayer.pause()
        }
        exoPlayer.addListener(listener)
        lifecycleOwner.lifecycle.addObserver(observer)
>>>>>>> 4e94ed7 (fix: unificar package y estabilizar consumo de API):app/src/main/java/com/example/akibazone/presentation/player/PlayerScreen.kt
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        when (val state = uiState) {
            is PlayerUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator(color = com.example.akibazone.ui.theme.Primary) }
            is PlayerUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { Text(state.message, color = com.example.akibazone.ui.theme.Error) }
            is PlayerUiState.Success -> {
                LaunchedEffect(state.videoUrl, exoPlayer) {
                    exoPlayer.setMediaItem(MediaItem.fromUri(state.videoUrl))
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                }
                
                AndroidView(
                    factory = { playerContext ->
                        PlayerView(playerContext)
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { playerView ->
                        playerView.player = exoPlayer
                    }
                )
            }
        }
        
        IconButton(onClick = onBackClick, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
        }
    }
}
