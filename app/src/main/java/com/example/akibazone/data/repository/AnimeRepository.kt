package com.example.akibazone.data.repository

import android.util.Log
import com.example.akibazone.data.local.AnimeDao
import com.example.akibazone.domain.model.Anime
import com.example.akibazone.domain.model.AnimeDetail
import com.example.akibazone.domain.model.Episode
import com.example.akibazone.data.network.AnimeScraper
import com.example.akibazone.data.network.AnilistApiService
import com.example.akibazone.data.network.AnilistQueries
import com.example.akibazone.data.network.JikanApiService
import kotlinx.coroutines.CancellationException
import com.example.akibazone.data.network.dto.AnilistData
import com.example.akibazone.data.network.dto.AnilistRequest
import com.example.akibazone.data.network.dto.JikanAnime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AnimeRepository(
    private val animeDao: AnimeDao,
    private val scraper: AnimeScraper,
    private val anilistService: AnilistApiService,
    private val jikanService: JikanApiService
) {
    private val TAG = "AnimeRepository"
    private val jikanCacheMutex = Mutex()
    private var jikanTopCache: List<Anime>? = null

    private suspend fun requestData(query: String): AnilistData {
        val response = anilistService.getAnimeList(AnilistRequest(query))
        if (!response.errors.isNullOrEmpty()) {
            throw IllegalStateException(response.errors.first().message ?: "Error de AniList")
        }
        return response.data ?: throw IllegalStateException("AniList no devolvió datos")
    }

    private fun JikanAnime.toDomain(): Anime {
        val id = id?.toString() ?: return Anime(id = "", title = "Sin título", imageUrl = "", link = "")
        return Anime(
            id = id,
            title = title ?: englishTitle ?: japaneseTitle ?: "Sin título",
            imageUrl = images?.jpg?.largeImageUrl ?: images?.jpg?.imageUrl.orEmpty(),
            link = id,
            type = type,
            rating = score?.toString() ?: "0.0",
            description = synopsis,
            genres = genres.mapNotNull { it.name },
            status = status,
            year = year?.toString(),
            episodesCount = episodes,
            studio = studios.firstOrNull()?.name
        )
    }

    private suspend fun getJikanTop(): List<Anime> = jikanCacheMutex.withLock {
        jikanTopCache ?: jikanService.getTopAnime().data.mapNotNull { anime ->
            anime.takeIf { it.id != null }?.toDomain()
        }.also { jikanTopCache = it }
    }

    private suspend fun getJikanSearch(
        query: String? = null,
        genre: String? = null,
        format: String? = null,
        orderBy: String? = null
    ): List<Anime> {
        if (query.isNullOrBlank() && genre.isNullOrBlank() && format.isNullOrBlank()) {
            return getJikanTop()
        }
        val genreIds = mapOf(
            "Action" to "1", "Adventure" to "2", "Comedy" to "4", "Drama" to "8",
            "Fantasy" to "10", "Horror" to "14", "Mahou Shoujo" to "16", "Mecha" to "18",
            "Music" to "19", "Mystery" to "7", "Psychological" to "40", "Romance" to "22",
            "Sci-Fi" to "24", "Slice of Life" to "36", "Sports" to "30",
            "Supernatural" to "37", "Thriller" to "41"
        )
        val type = when (format?.uppercase()) {
            "MOVIE" -> "movie"
            "TV" -> "tv"
            "OVA" -> "ova"
            "SPECIAL" -> "special"
            "ONA" -> "ona"
            else -> null
        }
        return jikanService.searchAnime(
            query = query?.takeUnless { it.isBlank() },
            genres = genreIds[genre],
            type = type,
            orderBy = orderBy,
            sort = if (orderBy == null) null else "desc"
        ).data.mapNotNull { anime -> anime.takeIf { it.id != null }?.toDomain() }
    }

    private suspend fun getJikanLatest(): List<Anime> =
        jikanService.searchAnime(orderBy = "start_date", sort = "desc").data.mapNotNull { anime ->
            anime.takeIf { it.id != null }?.toDomain()
        }

    private fun isAniListForbidden(error: Exception): Boolean =
        error is retrofit2.HttpException && error.code() == 403

    val history = animeDao.getAllHistory()
    val favorites = animeDao.getFavorites()

    suspend fun toggleFavorite(anime: Anime) = withContext(Dispatchers.IO) {
        val existing = animeDao.getAnimeById(anime.id)
        val dataAnime = com.example.akibazone.data.model.Anime(
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

    private fun com.example.akibazone.data.network.dto.Media.toDomain(): Anime {
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
            val response = requestData(query)
            response.page?.media?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "getTrendingAnime error: ${e.message}", e)
            if (isAniListForbidden(e)) Log.e(TAG, "AniList respondió HTTP 403; se usará Jikan")
            getJikanTop().ifEmpty { throw e }
        }
    }

    suspend fun getPopularAnime(): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val query = AnilistQueries.getPopularQuery()
            val response = requestData(query)
            val list = response.page?.media?.map { it.toDomain() } ?: emptyList()
            if (list.isNotEmpty()) list else scraper.getPopularAnime().map { it.toDomain() }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "getPopularAnime error: ${e.message}", e)
            if (isAniListForbidden(e)) Log.e(TAG, "AniList respondió HTTP 403; se usará Jikan")
            getJikanTop().ifEmpty { scraper.getPopularAnime().map { it.toDomain() } }.ifEmpty { throw e }
        }
    }

    suspend fun getLatestReleases(): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val query = AnilistQueries.getLatestReleasesQuery()
            val response = requestData(query)
            val list = response.page?.media?.map { it.toDomain() } ?: emptyList()
            if (list.isNotEmpty()) list else scraper.getLatestReleases().map { it.toDomain() }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "getLatestReleases error: ${e.message}", e)
            if (isAniListForbidden(e)) Log.e(TAG, "AniList respondió HTTP 403; se usará Jikan")
            getJikanLatest().ifEmpty {
                scraper.getLatestReleases().map { it.toDomain() }
            }.ifEmpty { throw e }
        }
    }

    suspend fun getTopRatedAnime(): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val query = AnilistQueries.getTopRatedQuery()
            val response = requestData(query)
            response.page?.media?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "getTopRatedAnime error: ${e.message}", e)
            if (isAniListForbidden(e)) Log.e(TAG, "AniList respondió HTTP 403; se usará Jikan")
            getJikanSearch(orderBy = "score").ifEmpty { throw e }
        }
    }

    suspend fun searchAnime(queryStr: String, genre: String? = null, format: String? = null, sort: String = "TRENDING_DESC"): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val query = AnilistQueries.getFilterQuery(search = queryStr, genre = genre, format = format, sort = sort)
            val response = requestData(query)
            response.page?.media?.map { it.toDomain() } ?: emptyList()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Log.e(TAG, "searchAnime error: ${e.message}", e)
            if (isAniListForbidden(e)) Log.e(TAG, "AniList respondió HTTP 403; se usará Jikan")
            getJikanSearch(
                query = queryStr,
                genre = genre,
                format = format,
                orderBy = sort.substringBefore("_").lowercase().takeUnless { it == "trending" }
            ).ifEmpty {
                if (genre == null && format == null && queryStr.isNotBlank()) {
                    scraper.searchAnime(queryStr).map { it.toDomain() }
                } else emptyList()
            }.ifEmpty { throw e }
        }
    }

    suspend fun getAnimeDetail(animeId: String): AnimeDetail? = withContext(Dispatchers.IO) {
        try {
            val idInt = animeId.toIntOrNull()
            if (idInt != null) {
                val query = AnilistQueries.getAnimeDetailQuery(idInt)
                val response = requestData(query)
                val media = response.media
                if (media != null) {
                    val anime = media.toDomain().copy(isFavorite = animeDao.getAnimeById(animeId)?.isFavorite ?: false)
                    // AniList ofrece metadatos, no enlaces para reproducir episodios.
                    val eps = emptyList<Episode>()
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
            if (e is CancellationException) throw e
            Log.e(TAG, "getAnimeDetail from AniList error: ${e.message}", e)
            if (isAniListForbidden(e)) Log.e(TAG, "AniList respondió HTTP 403; se usará Jikan")
            animeId.toIntOrNull()?.let { id ->
                val detail = jikanService.getAnimeDetail(id).data
                if (detail != null) {
                    val anime = detail.toDomain().copy(
                        isFavorite = animeDao.getAnimeById(animeId)?.isFavorite ?: false
                    )
                    return@withContext AnimeDetail(
                        anime = anime,
                        episodes = emptyList(),
                        japaneseTitle = detail.japaneseTitle,
                        englishTitle = detail.englishTitle,
                        status = detail.status,
                        year = detail.year?.toString(),
                        format = detail.type,
                        studio = detail.studios.firstOrNull()?.name,
                        trailerUrl = detail.trailer?.youtubeId?.let { "https://www.youtube.com/watch?v=$it" }
                    )
                }
            }
            if (animeId.toIntOrNull() != null) throw e
        }

        if (animeId.toIntOrNull() != null) return@withContext null
        // Los identificadores del scraper y AniList pertenecen a fuentes distintas.
        scraper.getAnimeDetail(animeId)?.let { detail ->
            AnimeDetail(
                anime = detail.anime.toDomain().copy(
                    description = detail.synopsis,
                    genres = detail.genres,
                    isFavorite = animeDao.getAnimeById(detail.anime.id)?.isFavorite ?: false
                ),
                episodes = detail.episodes.map { Episode(it.link, it.number, title = "Episodio ${it.number}") },
                japaneseTitle = detail.anime.title
            )
        }
    }

    suspend fun getVideoLinks(episodeLink: String): List<String> = withContext(Dispatchers.IO) {
        // ExoPlayer necesita un archivo multimedia; una página de servidor no basta.
        scraper.getVideoLinks(episodeLink).filter { link ->
            val path = java.net.URI(link).path.orEmpty().lowercase()
            path.endsWith(".m3u8") || path.endsWith(".mp4")
        }
    }

    suspend fun addToHistory(anime: Anime) = withContext(Dispatchers.IO) {
        val existing = animeDao.getAnimeById(anime.id)
        val dataAnime = com.example.akibazone.data.model.Anime(
            id = anime.id,
            title = anime.title,
            imageUrl = anime.imageUrl,
            link = anime.link,
            type = anime.type,
            rating = anime.rating,
            isFavorite = existing?.isFavorite ?: anime.isFavorite,
            timestamp = System.currentTimeMillis()
        )
        animeDao.insertAnime(dataAnime)
    }
}

fun com.example.akibazone.data.model.Anime.toDomain(): Anime {
    return Anime(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        link = this.link,
        type = this.type,
        lastEpisode = this.lastEpisode,
        rating = this.rating,
        isFavorite = this.isFavorite
    )
}
