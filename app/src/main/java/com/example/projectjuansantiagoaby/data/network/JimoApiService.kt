package com.example.projectjuansantiagoaby.data.network

import com.example.projectjuansantiagoaby.data.network.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JimoApiService {

    @GET("anime/flv/filter")
    suspend fun filterAnime(
        @Query("page") page: Int = 1,
        @Query("type") type: String? = null,
        @Query("genre") genre: String? = null
    ): AnimeResponse

    @GET("anime/flv/search")
    suspend fun searchAnime(
        @Query("q") query: String
    ): AnimeResponse

    @GET("anime/flv/info")
    suspend fun getAnimeInfo(
        @Query("id") id: String
    ): AnimeDetailDto

    @GET("anime/flv/servers")
    suspend fun getServers(
        @Query("id") id: String,
        @Query("episode") episode: String
    ): List<ServerDto>
}
