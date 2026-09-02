package com.example.projectjuansantiagoaby.data.model

data class Episode(
    val animeId: String,
    val number: String,
    val link: String,
    val title: String? = null,
    val imageUrl: String? = null
)
