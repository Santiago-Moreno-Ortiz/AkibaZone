package com.example.projectjuansantiagoaby.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_history")
data class Anime(
    @PrimaryKey val id: String, // Usualmente el slug de la URL
    val title: String,
    val imageUrl: String,
    val link: String,
    val lastEpisode: String? = null,
    val type: String? = null,
    val rating: String? = null,
    val timestamp: Long = System.currentTimeMillis() // Para el historial
)
