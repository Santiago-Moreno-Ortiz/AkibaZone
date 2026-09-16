package com.example.akibazone.domain.usecase

import com.example.akibazone.data.repository.AnimeRepository
import com.example.akibazone.domain.model.Anime

class AddToHistoryUseCase(private val repository: AnimeRepository) {
    suspend operator fun invoke(anime: Anime) {
        repository.addToHistory(anime)
    }
}
