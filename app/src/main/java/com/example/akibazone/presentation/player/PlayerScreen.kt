package com.example.akibazone.presentation.player

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberUpdatedState
import com.example.akibazone.domain.model.EpisodePlaybackType
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.akibazone.ui.theme.Error
import com.example.akibazone.ui.theme.Primary
import com.example.akibazone.data.network.PlaybackFormat

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    input: PlayerInput,
    viewModel: PlayerViewModel,
    isInPip: Boolean,
    onPipEligibilityChanged: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalActivity.current as ComponentActivity

    val uiState by viewModel.uiState.collectAsState()

    val demoVideo = remember(context, input) {
        if (input == PlayerInput.Demo) DemoVideo.resolve(context) else null
    }
    LaunchedEffect(input) {
        when (input) {
            is PlayerInput.Episode -> viewModel.loadVideo(input.id)
            PlayerInput.Demo -> viewModel.loadDemo(demoVideo)
        }
    }

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build()
    }

    val latestUiState by rememberUpdatedState(uiState)
    val reportPipEligibility by rememberUpdatedState(onPipEligibilityChanged)
    fun publishEligibility() {
        // Only a resolved direct PlaybackSource reaches Ready. EXTERNAL uses ACTION_VIEW.
        reportPipEligibility(isPipPlaybackAllowed(
            latestUiState,
            if ((latestUiState as? PlayerUiState.Ready)?.demo == null &&
                latestUiState is PlayerUiState.Ready) EpisodePlaybackType.DIRECT_STREAM else null,
            exoPlayer.currentMediaItem?.localConfiguration?.uri?.toString(),
            exoPlayer.isPlaying
        ))
    }
    SideEffect { publishEligibility() }
    val leavePlayer = {
        reportPipEligibility(false)
        exoPlayer.pause()
        onBackClick()
    }
    BackHandler(enabled = !isInPip, onBack = leavePlayer)

    DisposableEffect(exoPlayer, lifecycleOwner) {

        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                publishEligibility()
            }

            override fun onPlayerError(error: PlaybackException) {
                reportPipEligibility(false)
                viewModel.onPlaybackError()
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    // A visible PiP Activity is STARTED; STOP means it is no longer visible.
                    exoPlayer.pause()
                }

                else -> Unit
            }
        }

        exoPlayer.addListener(listener)
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.removeListener(listener)
            reportPipEligibility(false)
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        when (val state = uiState) {

            is PlayerUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Primary
                    )
                }
            }

            is PlayerUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = Error
                    )
                }
            }

            is PlayerUiState.NoSource -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se pudo obtener una fuente de reproducción para este episodio.",
                        color = Error
                    )
                }
            }

            is PlayerUiState.Ready -> {

                LaunchedEffect(state.source, exoPlayer) {
                    exoPlayer.setMediaItem(
                        MediaItem.Builder()
                            .setUri(state.source.url)
                            .setMimeType(
                                when (state.source.format) {
                                    PlaybackFormat.HLS -> MimeTypes.APPLICATION_M3U8
                                    PlaybackFormat.MP4 -> MimeTypes.VIDEO_MP4
                                }
                            )
                            .build()
                    )

                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                }

                AndroidView(
                    factory = { playerContext ->
                        PlayerView(playerContext).apply {
                            player = exoPlayer
                            useController = !isInPip
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { playerView ->
                        playerView.player = exoPlayer
                        playerView.useController = !isInPip
                        if (isInPip) playerView.hideController()
                    }
                )
            }
        }

        if (!isInPip && input == PlayerInput.Demo) {
            Text(
                text = "Video de demostración",
                color = Color.White,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp)
            )
        }

        if (!isInPip) IconButton(
            onClick = leavePlayer,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }
    }
}
