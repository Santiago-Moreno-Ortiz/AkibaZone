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
    val favorites = animeDao.getFavorites()

    suspend fun toggleFavorite(anime: Anime) = withContext(Dispatchers.IO) {
        val existing = animeDao.getAnimeById(anime.id)
        val dataAnime = com.example.projectjuansantiagoaby.data.model.Anime(
            id = anime.id,
            title = anime.title,
            imageUrl = anime.imageUrl,
            link = anime.link,
            type = anime.type,
            rating = anime.rating,
            isFavorite = !(existing?.isFavorite ?: false),
            timestamp = System.currentTimeMillis()
        )
        animeDao.insertAnime(dataAnime)
    }

    private fun com.example.projectjuansantiagoaby.data.network.dto.Media.toDomain(): Anime {
        return Anime(
            id = this.id?.toString() ?: "",
            title = this.title?.romaji ?: this.title?.english ?: this.title?.native ?: "Unknown",
            imageUrl = this.coverImage?.extraLarge ?: this.coverImage?.large ?: "",
            bannerUrl = this.bannerImage,
            link = this.id?.toString() ?: "",
            type = this.format ?: this.type,
            rating = (this.averageScore?.toFloat()?.div(10f))?.toString() ?: "0.0",
            description = this.description,
            genres = this.genres ?: emptyList(),
            status = this.status,
            year = this.seasonYear?.toString(),
            episodesCount = this.episodes,
            studio = this.studios?.nodes?.firstOrNull()?.name
        )
    }

    suspend fun getTrendingAnime(): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val query = AnilistQueries.getTrendingQuery()
            val response = anilistService.getAnimeList(com.example.projectjuansantiagoaby.data.network.dto.AnilistRequest(query))
            response.data?.page?.media?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getTrendingAnime error: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getPopularAnime(): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val query = AnilistQueries.getPopularQuery()
            val response = anilistService.getAnimeList(com.example.projectjuansantiagoaby.data.network.dto.AnilistRequest(query))
            val list = response.data?.page?.media?.map { it.toDomain() } ?: emptyList()
            if (list.isNotEmpty()) list else scraper.getPopularAnime().map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "getPopularAnime error: ${e.message}", e)
            scraper.getPopularAnime().map { it.toDomain() }
        }
    }

    suspend fun getLatestReleases(): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val query = AnilistQueries.getLatestReleasesQuery()
            val response = anilistService.getAnimeList(com.example.projectjuansantiagoaby.data.network.dto.AnilistRequest(query))
            val list = response.data?.page?.media?.map { it.toDomain() } ?: emptyList()
            if (list.isNotEmpty()) list else scraper.getLatestReleases().map { it.toDomain() }
        } catch (e: Exception) {
            Log.e(TAG, "getLatestReleases error: ${e.message}", e)
            scraper.getLatestReleases().map { it.toDomain() }
        }
    }

    suspend fun getTopRatedAnime(): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val query = AnilistQueries.getTopRatedQuery()
            val response = anilistService.getAnimeList(com.example.projectjuansantiagoaby.data.network.dto.AnilistRequest(query))
            response.data?.page?.media?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "getTopRatedAnime error: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun searchAnime(queryStr: String, genre: String? = null, format: String? = null, sort: String = "TRENDING_DESC"): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val query = AnilistQueries.getFilterQuery(search = queryStr, genre = genre, format = format, sort = sort)
            val response = anilistService.getAnimeList(com.example.projectjuansantiagoaby.data.network.dto.AnilistRequest(query))
            response.data?.page?.media?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "searchAnime error: ${e.message}", e)
            scraper.searchAnime(queryStr).map { it.toDomain() }
        }
    }

    suspend fun getAnimeDetail(animeId: String): AnimeDetail? = withContext(Dispatchers.IO) {
        try {
            val idInt = animeId.toIntOrNull()
            if (idInt != null) {
                val query = AnilistQueries.getAnimeDetailQuery(idInt)
                val response = anilistService.getAnimeList(com.example.projectjuansantiagoaby.data.network.dto.AnilistRequest(query))
                val media = response.data?.media
                if (media != null) {
                    val anime = media.toDomain()
                    val totalEpisodes = media.episodes ?: 12
                    val eps = (1..totalEpisodes).map { num ->
                        Episode(
                            id = "$animeId-$num",
                            number = num.toString(),
                            title = "Episodio $num"
                        )
                    }
                    return@withContext AnimeDetail(
                        anime = anime,
                        episodes = eps,
                        japaneseTitle = media.title?.native,
                        englishTitle = media.title?.english,
                        status = media.status,
                        year = media.seasonYear?.toString(),
                        format = media.format,
                        duration = media.duration?.let { "$it min" },
                        studio = media.studios?.nodes?.firstOrNull()?.name,
                        trailerUrl = if (media.trailer?.site == "youtube") "https://www.youtube.com/watch?v=${media.trailer?.id}" else null
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAnimeDetail from AniList error: ${e.message}", e)
        }

        // Fallback to scraper if AniList fails or ID is not numeric
        scraper.getAnimeDetail(animeId)?.let { detail ->
            AnimeDetail(
                anime = detail.anime.toDomain(),
                episodes = detail.episodes.map { Episode(it.animeId, it.number, title = "Episodio ${it.number}") },
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
