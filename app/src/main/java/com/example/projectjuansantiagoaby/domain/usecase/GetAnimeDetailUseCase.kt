package com.example.projectjuansantiagoaby.domain.usecase

import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import com.example.projectjuansantiagoaby.domain.model.AnimeDetail

class GetAnimeDetailUseCase(private val repository: AnimeRepository) {
    suspend operator fun invoke(animeId: String): AnimeDetail? {
        return repository.getAnimeDetail(animeId)
    }
}
