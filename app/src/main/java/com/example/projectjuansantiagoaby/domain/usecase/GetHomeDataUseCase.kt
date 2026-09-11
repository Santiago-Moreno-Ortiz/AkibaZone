package com.example.projectjuansantiagoaby.domain.usecase

import com.example.projectjuansantiagoaby.data.repository.AnimeRepository
import com.example.projectjuansantiagoaby.domain.model.Anime
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class HomeData(
    val popular: List<Anime>,
    val latest: List<Anime>,
    val trending: List<Anime>,
    val topRated: List<Anime>,
    val catalog: List<Anime> = emptyList()
)

class GetHomeDataUseCase(private val repository: AnimeRepository) {
    suspend operator fun invoke(): HomeData = coroutineScope {
        val popular = async { repository.getPopularAnime() }
        val latest = async { repository.getLatestReleases() }
        val trending = async { repository.getTrendingAnime() }
        val topRated = async { repository.getTopRatedAnime() }
        val catalog = async { repository.searchAnime("", sort = "POPULARITY_DESC") }
        
        val popularList = popular.await()
        val catalogList = catalog.await().let { if (it.isNotEmpty()) it else popularList }

        HomeData(
            popular = popularList,
            latest = latest.await(),
            trending = trending.await(),
            topRated = topRated.await(),
            catalog = catalogList
        )
    }
}
