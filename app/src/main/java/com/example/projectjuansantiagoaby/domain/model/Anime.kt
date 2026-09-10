package com.example.projectjuansantiagoaby.domain.model

data class Anime(
    val id: String,
    val title: String,
    val imageUrl: String,
    val bannerUrl: String? = null,
    val link: String,
    val type: String? = null,
    val lastEpisode: String? = null,
    val rating: String? = null,
    val description: String? = null,
    val isFavorite: Boolean = false,
    val genres: List<String> = emptyList(),
    val status: String? = null,
    val year: String? = null,
    val episodesCount: Int? = null,
    val studio: String? = null
)

data class Episode(
    val id: String,
    val number: String,
    val title: String? = null,
    val imageUrl: String? = null
)

data class AnimeDetail(
    val anime: Anime,
    val episodes: List<Episode>,
    val japaneseTitle: String? = null,
    val englishTitle: String? = null,
    val status: String? = null,
    val year: String? = null,
    val format: String? = null,
    val duration: String? = null,
    val studio: String? = null,
    val trailerUrl: String? = null
)
