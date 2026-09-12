package com.example.akibazone.domain.usecase

import com.example.akibazone.data.repository.AnimeRepository
import com.example.akibazone.domain.model.AnimeDetail

class GetAnimeDetailUseCase(private val repository: AnimeRepository) {
    suspend operator fun invoke(animeId: String): AnimeDetail? {
        return repository.getAnimeDetail(animeId)
    }
}
