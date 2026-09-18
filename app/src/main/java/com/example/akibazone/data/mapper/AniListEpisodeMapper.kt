package com.example.akibazone.data.mapper

import com.example.akibazone.data.network.dto.MediaStreamingEpisode
import com.example.akibazone.domain.model.Episode
import com.example.akibazone.domain.model.EpisodePlaybackType

object AniListEpisodeMapper {

    fun toDomain(episodes: List<MediaStreamingEpisode>?): List<Episode> =
        episodes.orEmpty().mapNotNull { episode ->
            val url = episode.url?.trim()?.takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            Episode(
                id = url,
                number = "",
                title = episode.title,
                imageUrl = episode.thumbnail,
                url = url,
                site = episode.site,
                playbackType = EpisodePlaybackType.EXTERNAL
            )
        }
}
