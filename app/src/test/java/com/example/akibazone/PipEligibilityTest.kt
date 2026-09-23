package com.example.akibazone

import com.example.akibazone.data.network.PlaybackFormat
import com.example.akibazone.data.network.PlaybackSource
import com.example.akibazone.domain.model.EpisodePlaybackType
import com.example.akibazone.presentation.player.PlayerUiState
import com.example.akibazone.presentation.player.isPipPlaybackAllowed
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PipEligibilityTest {
    // Synthetic URLs are policy inputs only; never playback fixtures or app sources.
    private val source = PlaybackSource("https://example.com/episode.m3u8", PlaybackFormat.HLS)
    private val ready = PlayerUiState.Ready(source)

    private fun allowed(
        state: PlayerUiState = ready,
        type: EpisodePlaybackType? = EpisodePlaybackType.DIRECT_STREAM,
        item: String? = source.url,
        playing: Boolean = true
    ) = isPipPlaybackAllowed(state, type, item, playing)

    @Test fun playingDirectStreamIsAllowed() { assertTrue(allowed()) }
    @Test fun playingMp4IsAllowed() {
        val mp4 = PlaybackSource("https://example.com/episode.mp4?token=test", PlaybackFormat.MP4)
        assertTrue(allowed(state = PlayerUiState.Ready(mp4), item = mp4.url))
    }
    @Test fun loadingIsRejected() { assertFalse(allowed(state = PlayerUiState.Loading)) }
    @Test fun errorIsRejected() { assertFalse(allowed(state = PlayerUiState.Error("failure"))) }
    @Test fun noSourceIsRejected() { assertFalse(allowed(state = PlayerUiState.NoSource)) }
    @Test fun externalIsRejectedEvenWithDirectLookingUrl() {
        assertFalse(allowed(type = EpisodePlaybackType.EXTERNAL))
    }
    @Test fun unknownTypeIsRejected() { assertFalse(allowed(type = null)) }
    @Test fun absentMediaItemIsRejected() { assertFalse(allowed(item = null)) }
    @Test fun staleMediaItemIsRejected() { assertFalse(allowed(item = "https://example.com/other.mp4")) }
    @Test fun notPlayingIsRejected() { assertFalse(allowed(playing = false)) }
    @Test fun providerPageIsRejected() {
        val page = "https://example.com/watch/episode"
        assertFalse(allowed(state = PlayerUiState.Ready(source.copy(url = page)), item = page))
    }
    @Test fun relativeOrNonHttpSourceIsRejected() {
        listOf("/episode.m3u8", "file:///episode.m3u8", "https:///episode.m3u8", "bad url").forEach {
            assertFalse(allowed(state = PlayerUiState.Ready(source.copy(url = it)), item = it))
        }
    }
    @Test fun mismatchedFormatIsRejected() {
        assertFalse(allowed(state = PlayerUiState.Ready(source.copy(format = PlaybackFormat.MP4))))
    }
}
