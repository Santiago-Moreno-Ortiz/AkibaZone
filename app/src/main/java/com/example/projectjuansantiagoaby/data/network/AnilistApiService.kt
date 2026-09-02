package com.example.projectjuansantiagoaby.data.network

import com.example.projectjuansantiagoaby.data.network.dto.AnilistResponse
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class AnilistRequest(
    @SerializedName("query") val query: String
)

interface AnilistApiService {
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/")
    suspend fun getAnimeList(@Body request: AnilistRequest): AnilistResponse
}
