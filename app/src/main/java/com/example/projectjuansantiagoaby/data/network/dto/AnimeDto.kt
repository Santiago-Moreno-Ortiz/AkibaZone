package com.example.projectjuansantiagoaby.data.network.dto

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

// AniList DTOs
data class AnilistResponse(
    @SerializedName("data") val data: AnilistData?
)

data class AnilistData(
    @SerializedName("Page") val page: Page?
)

data class Page(
    @SerializedName("media") val media: List<Media>?
)

data class Media(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: MediaTitle?,
    @SerializedName("coverImage") val coverImage: MediaCover?,
    @SerializedName("description") val description: String?,
    @SerializedName("averageScore") val averageScore: Int?,
    @SerializedName("type") val type: String?,
    @SerializedName("episodes") val episodes: Int?
)

data class MediaTitle(
    @SerializedName("romaji") val romaji: String?,
    @SerializedName("english") val english: String?,
    @SerializedName("native") val native: String?
)

data class MediaCover(
    @SerializedName("large") val large: String?,
    @SerializedName("extraLarge") val extraLarge: String?
)
