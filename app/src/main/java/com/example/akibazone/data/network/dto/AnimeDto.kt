package com.example.akibazone.data.network.dto

import com.google.gson.annotations.SerializedName

data class AnimeDto(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("poster") val poster: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("last_episode") val lastEpisode: String?
)

data class AnimeResponse(
    @SerializedName("data") val data: List<AnimeDto>?
)

data class AnimeDetailDto(
    @SerializedName("title") val title: String?,
    @SerializedName("synopsis") val synopsis: String?,
    @SerializedName("poster") val poster: String?,
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("episodes") val episodes: List<EpisodeDto>?
)

data class EpisodeDto(
    @SerializedName("id") val id: String?,
    @SerializedName("next_episode_date") val nextEpisodeDate: String?
)

data class ServerDto(
    @SerializedName("server") val server: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("code") val code: String?
)

// AniList GraphQL DTOs
data class AnilistRequest(
    @SerializedName("query") val query: String,
    @SerializedName("variables") val variables: Map<String, Any?>? = null
)

data class AnilistResponse(
    @SerializedName("data") val data: AnilistData?,
    @SerializedName("errors") val errors: List<AnilistError>? = null
)

data class AnilistError(val message: String?)

data class AnilistData(
    @SerializedName("Page") val page: Page?,
    @SerializedName("Media") val media: Media?
)

data class Page(
    @SerializedName("pageInfo") val pageInfo: PageInfo?,
    @SerializedName("media") val media: List<Media>?
)

data class PageInfo(
    @SerializedName("total") val total: Int?,
    @SerializedName("currentPage") val currentPage: Int?,
    @SerializedName("lastPage") val lastPage: Int?,
    @SerializedName("hasNextPage") val hasNextPage: Boolean?
)

data class Media(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: MediaTitle?,
    @SerializedName("coverImage") val coverImage: MediaCover?,
    @SerializedName("bannerImage") val bannerImage: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("averageScore") val averageScore: Int?,
    @SerializedName("format") val format: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("episodes") val episodes: Int?,
    @SerializedName("duration") val duration: Int?,
    @SerializedName("season") val season: String?,
    @SerializedName("seasonYear") val seasonYear: Int?,
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("studios") val studios: StudioConnection?,
    @SerializedName("nextAiringEpisode") val nextAiringEpisode: AiringSchedule?,
    @SerializedName("trailer") val trailer: MediaTrailer?
)

data class MediaTitle(
    @SerializedName("romaji") val romaji: String?,
    @SerializedName("english") val english: String?,
    @SerializedName("native") val native: String?
)

data class MediaCover(
    @SerializedName("large") val large: String?,
    @SerializedName("extraLarge") val extraLarge: String?,
    @SerializedName("color") val color: String?
)

data class StudioConnection(
    @SerializedName("nodes") val nodes: List<StudioNode>?
)

data class StudioNode(
    @SerializedName("name") val name: String?
)

data class AiringSchedule(
    @SerializedName("episode") val episode: Int?,
    @SerializedName("timeUntilAiring") val timeUntilAiring: Long?
)

data class MediaTrailer(
    @SerializedName("id") val id: String?,
    @SerializedName("site") val site: String?
)

// Jikan REST DTOs used as a public fallback when AniList is unavailable.
data class JikanResponse(
    @SerializedName("data") val data: List<JikanAnime> = emptyList()
)

data class JikanDetailResponse(
    @SerializedName("data") val data: JikanAnime?
)

data class JikanAnime(
    @SerializedName("mal_id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("title_english") val englishTitle: String?,
    @SerializedName("title_japanese") val japaneseTitle: String?,
    @SerializedName("images") val images: JikanImages?,
    @SerializedName("type") val type: String?,
    @SerializedName("episodes") val episodes: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("score") val score: Double?,
    @SerializedName("synopsis") val synopsis: String?,
    @SerializedName("year") val year: Int?,
    @SerializedName("genres") val genres: List<JikanNamedItem> = emptyList(),
    @SerializedName("studios") val studios: List<JikanNamedItem> = emptyList(),
    @SerializedName("trailer") val trailer: JikanTrailer?
)

data class JikanImages(
    @SerializedName("jpg") val jpg: JikanJpgImages?
)

data class JikanJpgImages(
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("large_image_url") val largeImageUrl: String?
)

data class JikanNamedItem(
    @SerializedName("name") val name: String?
)

data class JikanTrailer(
    @SerializedName("youtube_id") val youtubeId: String?
)
