package com.example.akibazone.data.network

import com.example.akibazone.data.network.dto.AnilistRequest
import com.example.akibazone.data.network.dto.AnilistResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AnilistApiService {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/")
    suspend fun getAnimeList(@Body request: AnilistRequest): AnilistResponse
}
