package com.example.akibazone

import com.example.akibazone.data.mapper.AniListEpisodeMapper
import com.example.akibazone.data.network.AnilistQueries
import com.example.akibazone.data.network.dto.MediaStreamingEpisode
import com.example.akibazone.domain.model.EpisodePlaybackType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AniListEpisodeMapperTest {

    @Test
    fun mapsAniListEpisodeAsExternalAndPreservesProviderData() {
        val episode = MediaStreamingEpisode(
            title = "Episode 1 - Asteroid Blues",
            thumbnail = "https://img.example/episode-1.jpg",
            url = "https://www.crunchyroll.com/watch/example",
            site = "Crunchyroll"
        )

        val result = AniListEpisodeMapper.toDomain(listOf(episode)).single()

        assertEquals(EpisodePlaybackType.EXTERNAL, result.playbackType)
        assertEquals("Crunchyroll", result.site)
        assertEquals("https://www.crunchyroll.com/watch/example", result.url)
        assertEquals("Episode 1 - Asteroid Blues", result.title)
        assertEquals("https://img.example/episode-1.jpg", result.imageUrl)
    }

    @Test
    fun mapsMissingAniListEpisodesToAnEmptyList() {
        assertTrue(AniListEpisodeMapper.toDomain(null).isEmpty())
        assertTrue(AniListEpisodeMapper.toDomain(emptyList()).isEmpty())
    }

    @Test
    fun ignoresAniListEpisodesWithoutAUrl() {
        val episode = MediaStreamingEpisode(
            title = "Episode 1",
            thumbnail = null,
            url = null,
            site = "Provider"
        )

        assertTrue(AniListEpisodeMapper.toDomain(listOf(episode)).isEmpty())
    }

    @Test
    fun detailQueryRequestsEveryStreamingEpisodeField() {
        val query = AnilistQueries.getAnimeDetailQuery(1)

        assertTrue(query.contains("streamingEpisodes"))
        assertTrue(query.contains("title"))
        assertTrue(query.contains("thumbnail"))
        assertTrue(query.contains("url"))
        assertTrue(query.contains("site"))
    }
}
