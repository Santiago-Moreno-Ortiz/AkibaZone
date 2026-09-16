package com.example.akibazone

import com.example.akibazone.data.model.Anime
import com.example.akibazone.presentation.profile.ProfileStats
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileStatsTest {

    @Test
    fun countsFavoritesAndHistoryFromRoomRows() {
        val stats = ProfileStats.from(
            favorites = listOf(storedAnime("favorite-1"), storedAnime("favorite-2")),
            history = listOf(storedAnime("history-1"), storedAnime("history-2"), storedAnime("history-3"))
        )

        assertEquals(2, stats.favoriteCount)
        assertEquals(3, stats.historyCount)
    }

    @Test
    fun returnsZeroWhenThereAreNoRows() {
        val stats = ProfileStats.from(emptyList(), emptyList())

        assertEquals(0, stats.favoriteCount)
        assertEquals(0, stats.historyCount)
    }

    private fun storedAnime(id: String) = Anime(
        id = id,
        title = id,
        imageUrl = "cover.jpg",
        link = id
    )
}
