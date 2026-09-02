package com.example.projectjuansantiagoaby.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.projectjuansantiagoaby.data.model.Anime

@Dao
interface AnimeDao {
    @Query("SELECT * FROM anime_history ORDER BY timestamp DESC")
    fun getAllHistory(): LiveData<List<Anime>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: Anime)

    @Query("DELETE FROM anime_history WHERE id = :animeId")
    suspend fun deleteAnime(animeId: String)
}
