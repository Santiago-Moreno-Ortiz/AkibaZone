package com.example.projectjuansantiagoaby.data.model

data class AnimeDetail(
    val anime: Anime,
    val synopsis: String,
    val genres: List<String>,
    val episodes: List<Episode>,
    val status: String? = null
)
