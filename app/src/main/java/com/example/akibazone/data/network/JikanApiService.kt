package com.example.akibazone.data.network

import com.example.akibazone.data.network.dto.JikanDetailResponse
import com.example.akibazone.data.network.dto.JikanResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Public REST fallback; it does not require an API key. */
interface JikanApiService {
    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("limit") limit: Int = 20
    ): JikanResponse

    @GET("anime")
    suspend fun searchAnime(
        @Query("q") query: String? = null,
        @Query("genres") genres: String? = null,
        @Query("type") type: String? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: Int = 30
    ): JikanResponse

    @GET("anime/{id}/full")
    suspend fun getAnimeDetail(@Path("id") id: Int): JikanDetailResponse
}
