package com.example.akibazone.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.akibazone.data.model.Anime

@Dao
interface AnimeDao {
    @Query("SELECT * FROM anime_history ORDER BY timestamp DESC")
    fun getAllHistory(): LiveData<List<Anime>>

    @Query("SELECT * FROM anime_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): LiveData<List<Anime>>

    @Query("SELECT * FROM anime_history WHERE id = :id")
    suspend fun getAnimeById(id: String): Anime?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: Anime)

    @Query("DELETE FROM anime_history WHERE id = :animeId")
    suspend fun deleteAnime(animeId: String)
}
