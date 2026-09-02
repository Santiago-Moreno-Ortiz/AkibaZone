package com.example.projectjuansantiagoaby.data.repository

import android.util.Log
import com.example.projectjuansantiagoaby.data.local.AnimeDao
import com.example.projectjuansantiagoaby.domain.model.Anime
import com.example.projectjuansantiagoaby.domain.model.AnimeDetail
import com.example.projectjuansantiagoaby.domain.model.Episode
import com.example.projectjuansantiagoaby.data.network.AnimeScraper
import com.example.projectjuansantiagoaby.data.network.JimoApiService
import com.example.projectjuansantiagoaby.data.network.AnilistApiService
import com.example.projectjuansantiagoaby.data.network.AnilistQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnimeRepository(
    private val animeDao: AnimeDao,
    private val scraper: AnimeScraper,
    private val apiService: JimoApiService,
    private val anilistService: AnilistApiService
) {
    private val TAG = "AnimeRepository"

    val history = animeDao.getAllHistory()

    suspend fun getPopularAnime(): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val query = AnilistQueries.getTrendingQuery()
            val response = anilistService.getAnimeList(com.example.projectjuansantiagoaby.data.network.AnilistRequest(query))
            val list = response.data?.page?.media?.map { media ->
                Anime(
                    id = media.id?.toString() ?: "",
                    title = media.title?.romaji ?: media.title?.english ?: "Unknown",
                    imageUrl = media.coverImage?.extraLarge ?: media.coverImage?.large ?: "",
                    link = media.id?.toString() ?: "",
                    type = media.type,
                    rating = (media.averageScore?.toFloat()?.div(10f))?.toString() ?: "0.0",
                    description = media.description
                )
            } ?: emptyList()
            Log.d(TAG, "getPopularAnime: Fetched ${list.size} items from Anilist")
            list
        } catch (e: Exception) {
            Log.e(TAG, "getPopularAnime error: ${e.message}", e)
            scraper.getPopularAnime().map { it.toDomain() }
        }
    }

    suspend fun getLatestReleases(): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val list = scraper.getLatestReleases().map { it.toDomain() }
            Log.d(TAG, "getLatestReleases: Fetched ${list.size} items")
            list
        } catch (e: Exception) {
            Log.e(TAG, "getLatestReleases error: ${e.message}")
            emptyList()
        }
    }

    suspend fun searchAnime(queryStr: String): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val query = AnilistQueries.getSearchQuery(queryStr)
            val response = anilistService.getAnimeList(com.example.projectjuansantiagoaby.data.network.AnilistRequest(query))
            val list = response.data?.page?.media?.map { media ->
                Anime(
                    id = media.id?.toString() ?: "",
                    title = media.title?.romaji ?: media.title?.english ?: "Unknown",
                    imageUrl = media.coverImage?.extraLarge ?: media.coverImage?.large ?: "",
                    link = media.id?.toString() ?: "",
                    type = media.type,
                    rating = (media.averageScore?.toFloat()?.div(10f))?.toString() ?: "0.0",
                    description = media.description
                )
            } ?: emptyList()
            Log.d(TAG, "searchAnime: Found ${list.size} results for $queryStr")
            list
        } catch (e: Exception) {
            Log.e(TAG, "searchAnime error: ${e.message}", e)
            scraper.searchAnime(queryStr).map { it.toDomain() }
        }
    }

    suspend fun getAnimeDetail(link: String): AnimeDetail? = withContext(Dispatchers.IO) {
        scraper.getAnimeDetail(link)?.let { detail ->
            AnimeDetail(
                anime = detail.anime.toDomain(),
                episodes = detail.episodes.map { Episode(it.animeId, it.number) },
                japaneseTitle = detail.anime.title
            )
        }
    }

    suspend fun getVideoLinks(episodeLink: String): List<String> = withContext(Dispatchers.IO) {
        val sampleLink = "https://cdn.tudominio.com/anime/123/episode-01/1080p/master.m3u8"
        try {
            val realLinks = scraper.getVideoLinks(episodeLink)
            if (realLinks.isNotEmpty()) realLinks else listOf(sampleLink)
        } catch (e: Exception) {
            listOf(sampleLink)
        }
    }

    suspend fun addToHistory(anime: Anime) = withContext(Dispatchers.IO) {
        // Mapear de Domain a Data para Room
        val dataAnime = com.example.projectjuansantiagoaby.data.model.Anime(
            id = anime.id,
            title = anime.title,
            imageUrl = anime.imageUrl,
            link = anime.link,
            type = anime.type,
            rating = anime.rating,
            timestamp = System.currentTimeMillis()
        )
        animeDao.insertAnime(dataAnime)
    }
}

// Extension function para mapear de Data a Domain
fun com.example.projectjuansantiagoaby.data.model.Anime.toDomain(): Anime {
    return Anime(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        link = this.link,
        type = this.type,
        lastEpisode = this.lastEpisode,
        rating = this.rating
    )
}
