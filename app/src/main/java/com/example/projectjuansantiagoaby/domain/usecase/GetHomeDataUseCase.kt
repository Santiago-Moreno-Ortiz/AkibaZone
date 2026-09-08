package com.example.projectjuansantiagoaby.domain.usecase

import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import com.example.projectjuansantiagoaby.domain.model.Anime
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class HomeData(
    val popular: List<Anime>,
    val latest: List<Anime>,
    val trending: List<Anime>
)

class GetHomeDataUseCase(private val repository: AnimeRepository) {
    suspend operator fun invoke(): HomeData = coroutineScope {
        val popular = async { repository.getPopularAnime() }
        val latest = async { repository.getLatestReleases() }
        
        val popularList = popular.await()
        HomeData(
            popular = popularList,
            latest = latest.await(),
            trending = popularList.take(5)
        )
    }
}
