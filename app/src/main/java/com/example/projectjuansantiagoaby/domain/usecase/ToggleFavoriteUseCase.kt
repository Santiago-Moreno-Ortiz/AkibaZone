package com.example.projectjuansantiagoaby.domain.usecase

import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import com.example.projectjuansantiagoaby.domain.model.Anime

class ToggleFavoriteUseCase(private val repository: AnimeRepository) {
    suspend operator fun invoke(anime: Anime) {
        repository.toggleFavorite(anime)
    }
}
