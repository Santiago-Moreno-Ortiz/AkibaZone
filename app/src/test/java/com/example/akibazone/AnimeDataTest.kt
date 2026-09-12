package com.example.akibazone

import com.example.akibazone.data.model.Anime
import com.example.akibazone.data.network.AnilistQueries
import com.example.akibazone.data.network.dto.AnilistResponse
import com.example.akibazone.data.repository.toDomain
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class AnimeDataTest {
    @Test
    fun searchPreservesQuotesBackslashesAndNewlines() {
        val search = "Título \"especial\"\\parte\nsegunda línea"
        val query = AnilistQueries.getFilterQuery(search = search)
        val mediaLine = query.lineSequence().first { it.contains("media(") }
        val encoded = mediaLine.substringAfter("search: ").substringBeforeLast(") {")
        assertEquals(search, Gson().fromJson(encoded, String::class.java))
        assertTrue(query.contains("type: ANIME"))
    }

    @Test
    fun studioResponseMatchesRequestedFields() {
        val query = AnilistQueries.getAnimeDetailQuery(1)
        assertTrue(query.contains("nodes {"))
        val response = Gson().fromJson(
            """{"data":{"Media":{"id":1,"studios":{"nodes":[{"name":"Sunrise"}]}}}}""",
            AnilistResponse::class.java
        )
        assertEquals("Sunrise", response.data?.media?.studios?.nodes?.first()?.name)
    }

    @Test
    fun graphqlErrorsAreAvailableEvenWithoutData() {
        val response = Gson().fromJson(
            """{"data":null,"errors":[{"message":"Service unavailable","status":403}]}""",
            AnilistResponse::class.java
        )
        assertNull(response.data)
        assertEquals("Service unavailable", response.errors?.first()?.message)
    }

    @Test
    fun localFavoriteKeepsItsStateAndIdentityInTheUiModel() {
        val saved = Anime("1", "Cowboy Bebop", "cover.jpg", "1", isFavorite = true)
        assertTrue(saved.toDomain().isFavorite)
        assertEquals("1", saved.toDomain().id)
        assertFalse(saved.copy(isFavorite = false).toDomain().isFavorite)
    }
}
