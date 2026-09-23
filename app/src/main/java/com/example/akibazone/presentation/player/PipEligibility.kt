package com.example.akibazone.presentation.player

import com.example.akibazone.data.network.PlaybackSourceClassifier
import com.example.akibazone.domain.model.EpisodePlaybackType
import java.net.URI

/** Pure policy; Activity additionally checks API level, device support and current destination. */
fun isPipPlaybackAllowed(
    uiState: PlayerUiState,
    playbackType: EpisodePlaybackType?,
    mediaItemUri: String?,
    isPlaying: Boolean
): Boolean {
    if (!isPlaying || playbackType == EpisodePlaybackType.EXTERNAL) return false
    val ready = uiState as? PlayerUiState.Ready ?: return false
    val source = ready.source
    ready.demo?.let { demo ->
        return playbackType == null && source.url == demo.uri && mediaItemUri == demo.uri &&
            source.format == com.example.akibazone.data.network.PlaybackFormat.MP4
    }
    if (playbackType != EpisodePlaybackType.DIRECT_STREAM) return false
    val uri = runCatching { URI(source.url) }.getOrNull() ?: return false
    return uri.scheme in listOf("https", "http") && !uri.host.isNullOrBlank() &&
        PlaybackSourceClassifier.classify(source.url) == source && mediaItemUri == source.url
}
