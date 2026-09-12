package com.example.akibazone.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val ANILIST_URL = "https://graphql.anilist.co/"
    private const val JIKAN_URL = "https://api.jikan.moe/v4/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val anilistRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ANILIST_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val jikanRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(JIKAN_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val anilistService: AnilistApiService by lazy {
        anilistRetrofit.create(AnilistApiService::class.java)
    }

    val jikanService: JikanApiService by lazy {
        jikanRetrofit.create(JikanApiService::class.java)
    }
}
