package com.example.akibazone.data.network

import com.example.akibazone.data.model.Anime
import java.net.URI

/** A direct media URL that Media3 can consume without opening a provider page. */
data class PlaybackSource(
    val url: String,
    val format: PlaybackFormat
)

enum class PlaybackFormat {
    HLS,
    MP4
}

/**
 * Keeps the relationship with AnimeFLV explicit: the slug always comes from a
 * verified AnimeFLV search result, never from an AniList or Jikan numeric id.
 */
object AnimeFlvPlaybackMapper {

    fun findExactTitleMatch(requestedTitle: String, candidates: List<Anime>): Anime? {
        val normalizedRequestedTitle = normalizeTitle(requestedTitle)
        if (normalizedRequestedTitle.isBlank()) return null

        return candidates.firstOrNull { candidate ->
            normalizeTitle(candidate.title) == normalizedRequestedTitle
        }
    }

    fun episodeLink(slug: String, episodeNumber: String): String =
        "/ver/$slug-$episodeNumber"

    private fun normalizeTitle(title: String): String =
        java.text.Normalizer.normalize(title, java.text.Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "")
}

object PlaybackSourceClassifier {

    fun classify(url: String): PlaybackSource? {
        val path = runCatching { URI(url).path.orEmpty().lowercase() }.getOrNull() ?: return null
        val format = when {
            path.endsWith(".m3u8") -> PlaybackFormat.HLS
            path.endsWith(".mp4") -> PlaybackFormat.MP4
            else -> return null
        }
        return PlaybackSource(url = url, format = format)
    }
}
