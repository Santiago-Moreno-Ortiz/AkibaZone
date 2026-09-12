package com.example.akibazone.domain.usecase

import com.example.akibazone.data.repository.AnimeRepository
import com.example.akibazone.domain.model.Anime
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.CancellationException

data class HomeData(
    val popular: List<Anime>,
    val latest: List<Anime>,
    val trending: List<Anime>,
    val topRated: List<Anime>,
    val catalog: List<Anime> = emptyList()
)

class GetHomeDataUseCase(private val repository: AnimeRepository) {
    // Una sección fallida no descarta las demás; el fallo total sí se muestra.
    private suspend fun section(load: suspend () -> List<Anime>): List<Anime> = try {
        load()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        emptyList()
    }

    suspend operator fun invoke(): HomeData = supervisorScope {
        val popular = async { section { repository.getPopularAnime() } }
        val latest = async { section { repository.getLatestReleases() } }
        val trending = async { section { repository.getTrendingAnime() } }
        val topRated = async { section { repository.getTopRatedAnime() } }
        val catalog = async { section { repository.searchAnime("", sort = "POPULARITY_DESC") } }
        
        val popularList = popular.await()
        val catalogList = catalog.await().let { if (it.isNotEmpty()) it else popularList }

        val result = HomeData(
            popular = popularList,
            latest = latest.await(),
            trending = trending.await(),
            topRated = topRated.await(),
            catalog = catalogList
        )
        if (listOf(result.popular, result.latest, result.trending, result.topRated, result.catalog).all { it.isEmpty() }) {
            throw IllegalStateException("No se pudo cargar el catálogo. Comprueba tu conexión o reintenta más tarde.")
        }
        result
    }
}
