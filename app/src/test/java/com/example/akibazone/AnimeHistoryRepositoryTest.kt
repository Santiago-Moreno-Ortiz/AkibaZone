package com.example.akibazone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.akibazone.data.local.AnimeDao
import com.example.akibazone.data.model.Anime as StoredAnime
import com.example.akibazone.data.network.AnilistApiService
import com.example.akibazone.data.network.AnimeScraper
import com.example.akibazone.data.network.JikanApiService
import com.example.akibazone.data.network.dto.AnilistRequest
import com.example.akibazone.data.network.dto.AnilistResponse
import com.example.akibazone.data.network.dto.JikanDetailResponse
import com.example.akibazone.data.network.dto.JikanResponse
import com.example.akibazone.data.repository.AnimeRepository
import com.example.akibazone.domain.model.Anime
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimeHistoryRepositoryTest {

    @Test
    fun historyIsReadFromTheDao() {
        val dao = FakeAnimeDao(
            mutableListOf(
                storedAnime(id = "1", title = "Cowboy Bebop")
            )
        )

        val history = repository(dao).history.value.orEmpty()

        assertEquals(listOf("1"), history.map { it.id })
    }

    @Test
    fun addingHistoryDoesNotRemoveAnExistingFavoriteFlag() = runBlocking {
        val dao = FakeAnimeDao(
            mutableListOf(
                storedAnime(id = "1", title = "Cowboy Bebop", isFavorite = true)
            )
        )

        repository(dao).addToHistory(
            Anime(
                id = "1",
                title = "Cowboy Bebop actualizado",
                imageUrl = "cover.jpg",
                link = "1"
            )
        )

        assertTrue(dao.rows.single().isFavorite)
        assertEquals("Cowboy Bebop actualizado", dao.rows.single().title)
    }

    @Test
    fun clearingHistoryKeepsFavoriteRows() = runBlocking {
        val dao = FakeAnimeDao(
            mutableListOf(
                storedAnime(id = "history", title = "Solo historial"),
                storedAnime(id = "favorite", title = "Favorito", isFavorite = true)
            )
        )

        repository(dao).clearHistory()

        assertEquals(listOf("favorite"), dao.rows.map { it.id })
    }

    private fun repository(dao: AnimeDao) = AnimeRepository(
        animeDao = dao,
        scraper = AnimeScraper(),
        anilistService = object : AnilistApiService {
            override suspend fun getAnimeList(request: AnilistRequest): AnilistResponse =
                AnilistResponse(data = null)
        },
        jikanService = object : JikanApiService {
            override suspend fun getTopAnime(limit: Int): JikanResponse = JikanResponse()

            override suspend fun searchAnime(
                query: String?,
                genres: String?,
                type: String?,
                orderBy: String?,
                sort: String?,
                limit: Int
            ): JikanResponse = JikanResponse()

            override suspend fun getAnimeDetail(id: Int): JikanDetailResponse =
                JikanDetailResponse(data = null)
        }
    )

    private fun storedAnime(
        id: String,
        title: String,
        isFavorite: Boolean = false
    ) = StoredAnime(
        id = id,
        title = title,
        imageUrl = "cover.jpg",
        link = id,
        isFavorite = isFavorite
    )

    private class FakeAnimeDao(initialRows: MutableList<StoredAnime>) : AnimeDao {
        val rows = initialRows
        private val historyLiveData = MutableLiveData(rows.toList())

        override fun getAllHistory(): LiveData<List<StoredAnime>> = historyLiveData

        override fun getFavorites(): LiveData<List<StoredAnime>> =
            MutableLiveData(rows.filter { it.isFavorite })

        override suspend fun getAnimeById(id: String): StoredAnime? =
            rows.firstOrNull { it.id == id }

        override suspend fun insertAnime(anime: StoredAnime) {
            rows.removeAll { it.id == anime.id }
            rows += anime
        }

        override suspend fun deleteAnime(animeId: String) {
            rows.removeAll { it.id == animeId }
        }

        override suspend fun clearHistory() {
            rows.removeAll { !it.isFavorite }
        }
    }
}
