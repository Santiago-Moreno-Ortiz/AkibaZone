package com.example.akibazone

import com.example.akibazone.data.model.Anime
import com.example.akibazone.data.network.AnimeFlvPlaybackMapper
import com.example.akibazone.data.network.PlaybackFormat
import com.example.akibazone.data.network.PlaybackSourceClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PlaybackSourceTest {

    @Test
    fun classifiesHlsWhenTheUrlHasQueryParameters() {
        val source = PlaybackSourceClassifier.classify("https://example.com/master.m3u8?token=abc")

        assertEquals(PlaybackFormat.HLS, source?.format)
    }

    @Test
    fun classifiesMp4WhenTheUrlHasQueryParameters() {
        val source = PlaybackSourceClassifier.classify("https://example.com/video.mp4?expires=123")

        assertEquals(PlaybackFormat.MP4, source?.format)
    }

    @Test
    fun rejectsAProviderPageThatIsNotADirectMediaSource() {
        assertNull(PlaybackSourceClassifier.classify("https://example.com/embed/episode"))
    }

    @Test
    fun rejectsCrunchyrollAndYouTubePagesAsDirectMediaSources() {
        assertNull(PlaybackSourceClassifier.classify("https://www.crunchyroll.com/watch/EXAMPLE/episode"))
        assertNull(PlaybackSourceClassifier.classify("https://www.youtube.com/watch?v=example"))
    }

    @Test
    fun keepsTheSlugReturnedByTheVerifiedAnimeFlvSearch() {
        val candidate = Anime(
            id = "cowboy-bebop",
            title = "Cowboy Bebop",
            imageUrl = "",
            link = "/anime/cowboy-bebop"
        )

        val match = AnimeFlvPlaybackMapper.findExactTitleMatch("Cowboy BéBop", listOf(candidate))

        assertEquals("cowboy-bebop", match?.id)
        assertEquals("/ver/cowboy-bebop-1", AnimeFlvPlaybackMapper.episodeLink(match!!.id, "1"))
    }

    @Test
    fun doesNotMapANameThatDoesNotMatchExactly() {
        val candidate = Anime(
            id = "cowboy-bebop",
            title = "Cowboy Bebop",
            imageUrl = "",
            link = "/anime/cowboy-bebop"
        )

        assertNull(AnimeFlvPlaybackMapper.findExactTitleMatch("Cowboy Bebop: La película", listOf(candidate)))
    }
}
