package com.example.projectjuansantiagoaby.domain.model

data class Anime(
    val id: String,
    val title: String,
    val imageUrl: String,
    val link: String,
    val type: String? = null,
    val lastEpisode: String? = null,
    val rating: String? = null,
    val description: String? = null,
    val genres: List<String> = emptyList()
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
    val status: String? = null,
    val year: String? = null
)
