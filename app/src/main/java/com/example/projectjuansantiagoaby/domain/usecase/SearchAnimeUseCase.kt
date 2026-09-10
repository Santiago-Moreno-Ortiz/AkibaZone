package com.example.projectjuansantiagoaby.domain.usecase

import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import com.example.projectjuansantiagoaby.domain.model.Anime

class SearchAnimeUseCase(private val repository: AnimeRepository) {
    suspend operator fun invoke(
        query: String = "",
        genre: String? = null,
        format: String? = null,
        sort: String = "TRENDING_DESC"
    ): List<Anime> {
        return repository.searchAnime(query, genre, format, sort)
    }
}
