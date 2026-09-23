package com.example.akibazone

import com.example.akibazone.data.network.PlaybackFormat
import com.example.akibazone.data.network.PlaybackSource
import com.example.akibazone.domain.model.EpisodePlaybackType
import com.example.akibazone.presentation.player.DemoVideo
import com.example.akibazone.presentation.player.PlayerUiState
import com.example.akibazone.presentation.player.isPipPlaybackAllowed
import org.junit.Assert.*
import org.junit.Test

class DemoPipEligibilityTest {
    private val demo = DemoVideo.fromResource("com.example.akibazone", 123)!!
    private val ready = PlayerUiState.Ready(PlaybackSource(demo.uri, PlaybackFormat.MP4), demo)
    private fun allowed(state: PlayerUiState = ready, playing: Boolean = true) =
        isPipPlaybackAllowed(state, null, demo.uri, playing)

    @Test fun demoLoadsWithoutRepository() {
        val model = com.example.akibazone.presentation.player.PlayerViewModel()
        model.loadDemo(demo)
        val state = model.uiState.value as PlayerUiState.Ready
        assertEquals(demo.uri, state.source.url)
        assertSame(demo, state.demo)
    }
    @Test fun absentDemoProducesControlledError() {
        val model = com.example.akibazone.presentation.player.PlayerViewModel()
        model.loadDemo(null)
        assertTrue(model.uiState.value is PlayerUiState.Error)
    }
    @Test fun controlledDemoAllowsPip() { assertTrue(allowed()) }
    @Test fun loadingDoesNotAllowPip() { assertFalse(allowed(PlayerUiState.Loading)) }
    @Test fun errorDoesNotAllowPip() { assertFalse(allowed(PlayerUiState.Error("error"))) }
    @Test fun noSourceDoesNotAllowPip() { assertFalse(allowed(PlayerUiState.NoSource)) }
    @Test fun inactiveDemoDoesNotAllowPip() { assertFalse(allowed(playing = false)) }
    @Test fun missingResourceHasNoDemo() { assertNull(DemoVideo.fromResource("com.example.akibazone", 0)) }
    @Test fun invalidPackageCannotCreateDemo() { assertNull(DemoVideo.fromResource("evil/path", 123)) }
    @Test fun externalNeverAllowsDemoPip() {
        assertFalse(isPipPlaybackAllowed(ready, EpisodePlaybackType.EXTERNAL, demo.uri, true))
    }
    @Test fun arbitraryLocalUrisAreNotDemo() {
        listOf("file:///demo.mp4", "content://video/demo.mp4", "javascript:video.mp4", demo.uri).forEach { uri ->
            val state = PlayerUiState.Ready(PlaybackSource(uri, PlaybackFormat.MP4))
            assertFalse(isPipPlaybackAllowed(state, null, uri, true))
            assertFalse(isPipPlaybackAllowed(state, EpisodePlaybackType.DIRECT_STREAM, uri, true))
        }
    }
    @Test fun differentResourceCannotUseDemoCapability() {
        val uri = "android.resource://com.example.akibazone/456"
        val state = ready.copy(source = PlaybackSource(uri, PlaybackFormat.MP4))
        assertFalse(isPipPlaybackAllowed(state, null, uri, true))
    }
    @Test fun mismatchedMediaItemRejected() {
        assertFalse(isPipPlaybackAllowed(ready, null, "android.resource://other.app/123", true))
    }
    @Test fun demoMustBeMp4() {
        assertFalse(allowed(ready.copy(source = ready.source.copy(format = PlaybackFormat.HLS))))
    }
}
